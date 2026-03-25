<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\RajaOngkirService;
use Illuminate\Http\Request;

class ShippingController extends Controller
{
    public function __construct(private RajaOngkirService $rajaOngkir) {}

    /** GET /api/v1/shipping/provinces */
    public function provinces()
    {
        try {
            $provinces = $this->rajaOngkir->getProvinces();
            return response()->json(['status' => 'success', 'data' => $provinces]);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => $e->getMessage()], 503);
        }
    }

    /** GET /api/v1/shipping/cities?province_id=11 */
    public function cities(Request $request)
    {
        $provinceId = $request->query('province_id');
        if (!$provinceId) {
            return response()->json(['status' => 'error', 'message' => 'province_id is required'], 422);
        }

        try {
            $cities = $this->rajaOngkir->getCities($provinceId);
            return response()->json(['status' => 'success', 'data' => $cities]);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => $e->getMessage()], 503);
        }
    }

    /** GET /api/v1/shipping/postal-code?city_id=3273 */
    public function postalCode(Request $request)
    {
        $cityId = $request->query('city_id');
        if (!$cityId) {
            return response()->json(['status' => 'error', 'message' => 'city_id is required'], 422);
        }

        try {
            $postalCode = $this->rajaOngkir->getPostalCode($cityId);
            return response()->json(['status' => 'success', 'postal_code' => $postalCode]);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => $e->getMessage()], 503);
        }
    }

    /**
     * POST /api/v1/shipping/cost
     * Body: { destination_city_id, weight (grams), couriers[] }
     * couriers: array of "jne","tiki","pos"
     */
    public function cost(Request $request)
    {
        $request->validate([
            'destination_city_id' => 'required|string',
            'weight'              => 'required|integer|min:1',
            'couriers'            => 'nullable|array',
            'couriers.*'          => 'in:jne,jnt,tiki,pos',
        ]);

        $couriers = $request->couriers ?? ['jne', 'jnt', 'tiki', 'pos'];
        try {
            $results = [];
            foreach ($couriers as $courier) {
                $services = $this->rajaOngkir->getCost(
                    $request->destination_city_id,
                    $request->weight,
                    $courier
                );
                foreach ($services as $service) {
                    $results[] = $service; // already formatted by service
                }
            }

            // Sort cheapest first
            usort($results, fn($a, $b) => $a['cost'] <=> $b['cost']);

            return response()->json(['status' => 'success', 'data' => $results]);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => $e->getMessage()], 503);
        }
    }
}
