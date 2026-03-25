<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Voucher;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class VoucherController extends Controller
{
    /**
     * POST /api/v1/vouchers/apply
     * Validate a voucher code and return the discount.
     */
    public function apply(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'code'     => 'required|string|max:50',
            'subtotal' => 'required|numeric|min:0',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status'  => 'error',
                'message' => $validator->errors()->first(),
            ], 422);
        }

        $voucher = Voucher::where('code', strtoupper(trim($request->code)))->first();

        if (!$voucher) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Kode voucher tidak ditemukan.',
            ], 404);
        }

        if (!$voucher->isValid()) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Voucher sudah tidak berlaku atau habis digunakan.',
            ], 422);
        }

        $subtotal = (float) $request->subtotal;

        if ($subtotal < (float) $voucher->min_purchase) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Minimum pembelian untuk voucher ini adalah Rp ' . number_format($voucher->min_purchase, 0, ',', '.'),
            ], 422);
        }

        $discountAmount = $voucher->calculateDiscount($subtotal);

        return response()->json([
            'status'  => 'success',
            'message' => 'Voucher berhasil diterapkan.',
            'data'    => [
                'code'            => $voucher->code,
                'description'     => $voucher->description,
                'type'            => $voucher->type,
                'value'           => (float) $voucher->value,
                'discount_amount' => $discountAmount,
            ],
        ]);
    }
}
