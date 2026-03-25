<?php

namespace App\Services;

use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Cache;

class RajaOngkirService
{
    private const EMSIFA_BASE = 'https://www.emsifa.com/api-wilayah-indonesia/api/';
    private const CACHE_TTL   = 86400; // 24 jam

    // Province IDs per pulau/zona
    private const ZONES = [
        'jawa'       => ['31','32','33','34','35','36'],
        'bali_ntt'   => ['51','52','53'],
        'kalimantan' => ['61','62','63','64','65'],
        'sulawesi'   => ['71','72','73','74','75','76'],
        'maluku'     => ['81','82'],
        'papua'      => ['91','92','94'],
        'sumatera'   => ['11','12','13','14','15','16','17','18','19','21'],
    ];

    // Tarif per zona per 1kg (Rupiah). Dikalikan ceil(weight/1000).
    private const RATES = [
        'jawa' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>9000,'etd'=>'1-2 hari'],
                       ['service'=>'YES','description'=>'Ekspres','cost'=>19000,'etd'=>'1 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>8000,'etd'=>'1-2 hari']],
            'tiki' => [['service'=>'REG','description'=>'Reguler','cost'=>10000,'etd'=>'1-2 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>7000,'etd'=>'2-3 hari']],
        ],
        'bali_ntt' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>15000,'etd'=>'2-3 hari'],
                       ['service'=>'YES','description'=>'Ekspres','cost'=>28000,'etd'=>'1 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>14000,'etd'=>'2-3 hari']],
            'tiki' => [['service'=>'REG','description'=>'Reguler','cost'=>16000,'etd'=>'2-3 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>13000,'etd'=>'3-4 hari']],
        ],
        'sumatera' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>18000,'etd'=>'3-5 hari'],
                       ['service'=>'YES','description'=>'Ekspres','cost'=>35000,'etd'=>'2 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>16000,'etd'=>'3-5 hari']],
            'tiki' => [['service'=>'REG','description'=>'Reguler','cost'=>19000,'etd'=>'3-5 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>15000,'etd'=>'4-6 hari']],
        ],
        'kalimantan' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>22000,'etd'=>'3-5 hari'],
                       ['service'=>'YES','description'=>'Ekspres','cost'=>40000,'etd'=>'2 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>20000,'etd'=>'3-5 hari']],
            'tiki' => [['service'=>'REG','description'=>'Reguler','cost'=>23000,'etd'=>'3-5 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>18000,'etd'=>'4-7 hari']],
        ],
        'sulawesi' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>25000,'etd'=>'3-6 hari'],
                       ['service'=>'YES','description'=>'Ekspres','cost'=>45000,'etd'=>'2 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>23000,'etd'=>'3-6 hari']],
            'tiki' => [['service'=>'REG','description'=>'Reguler','cost'=>26000,'etd'=>'3-6 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>20000,'etd'=>'5-8 hari']],
        ],
        'maluku' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>35000,'etd'=>'4-7 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>33000,'etd'=>'4-7 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>28000,'etd'=>'6-9 hari']],
        ],
        'papua' => [
            'jne'  => [['service'=>'REG','description'=>'Reguler','cost'=>45000,'etd'=>'5-9 hari']],
            'jnt'  => [['service'=>'EZ','description'=>'Reguler','cost'=>42000,'etd'=>'5-9 hari']],
            'pos'  => [['service'=>'REG','description'=>'Pos Reguler','cost'=>35000,'etd'=>'7-12 hari']],
        ],
    ];

    /** Get all provinces — cached 24 jam */
    public function getProvinces(): array
    {
        return Cache::remember('provinces_emsifa', self::CACHE_TTL, function () {
            $response = Http::withoutVerifying()->get(self::EMSIFA_BASE . 'provinces.json');
            if (!$response->successful()) {
                throw new \Exception('Gagal mengambil data provinsi');
            }
            return array_map(fn($p) => [
                'province_id' => $p['id'],
                'province'    => $p['name'],
            ], $response->json());
        });
    }

    /** Get cities by province — cached 24 jam */
    public function getCities(string $provinceId): array
    {
        return Cache::remember("cities_emsifa_{$provinceId}", self::CACHE_TTL, function () use ($provinceId) {
            $response = Http::withoutVerifying()->get(self::EMSIFA_BASE . "regencies/{$provinceId}.json");
            if (!$response->successful()) {
                throw new \Exception('Gagal mengambil data kota');
            }
            return array_map(function ($c) use ($provinceId) {
                $name = $c['name'];
                $type = 'Kabupaten';
                $cityName = $name;
                if (str_starts_with($name, 'Kota ')) {
                    $type = 'Kota';
                    $cityName = substr($name, 5);
                } elseif (str_starts_with($name, 'Kabupaten ')) {
                    $type = 'Kabupaten';
                    $cityName = substr($name, 10);
                }
                return [
                    'city_id'     => $c['id'],
                    'province_id' => $provinceId,
                    'type'        => $type,
                    'city_name'   => $cityName,
                    'postal_code' => '',
                ];
            }, $response->json());
        });
    }

    /**
     * Get a representative postal code for a regency/city by fetching its first
     * district, then the first village of that district — cached 24 jam.
     */
    public function getPostalCode(string $cityId): string
    {
        return Cache::remember("postal_code_{$cityId}", self::CACHE_TTL, function () use ($cityId) {
            try {
                $distResponse = Http::withoutVerifying()
                    ->timeout(5)
                    ->get(self::EMSIFA_BASE . "districts/{$cityId}.json");
                if (!$distResponse->successful()) return '';

                $districts = $distResponse->json();
                if (empty($districts)) return '';

                $villageResponse = Http::withoutVerifying()
                    ->timeout(5)
                    ->get(self::EMSIFA_BASE . "villages/{$districts[0]['id']}.json");
                if (!$villageResponse->successful()) return '';

                $villages = $villageResponse->json();
                return $villages[0]['postal_code'] ?? '';
            } catch (\Exception $e) {
                return '';
            }
        });
    }

    /** Hitung ongkir berdasarkan zona pulau */
    public function getCost(string $destinationCityId, int $weightGrams, string $courier): array
    {
        // Province ID = 2 digit pertama dari city ID (format emsifa)
        $destProvinceId = substr($destinationCityId, 0, 2);
        $zone           = $this->getZone($destProvinceId);
        $multiplier     = max(1, (int) ceil($weightGrams / 1000));
        $courierLower   = strtolower($courier);
        $rates          = self::RATES[$zone][$courierLower] ?? [];

        return array_map(fn($rate) => [
            'courier'     => strtoupper($courier),
            'service'     => $rate['service'],
            'description' => $rate['description'],
            'cost'        => $rate['cost'] * $multiplier,
            'etd'         => $rate['etd'],
        ], $rates);
    }

    private function getZone(string $provinceId): string
    {
        foreach (self::ZONES as $zone => $ids) {
            if (in_array($provinceId, $ids)) {
                return $zone;
            }
        }
        return 'jawa'; // fallback
    }
}
