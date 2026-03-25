<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;

class PointsController extends Controller
{
    /**
     * GET /api/v1/points
     * Returns the authenticated user's points balance and history.
     */
    public function index(Request $request)
    {
        /** @var User $user */
        $user = $request->user();

        $history = $user->pointsHistory()
            ->with('order:id,order_number')
            ->take(50)
            ->get()
            ->map(function ($h) {
                return [
                    'id'          => $h->id,
                    'points'      => $h->points,
                    'type'        => $h->type,
                    'description' => $h->description,
                    'order_id'    => $h->order_id,
                    'order_number'=> $h->order?->order_number,
                    'created_at'  => $h->created_at?->toIso8601String(),
                ];
            });

        return response()->json([
            'status' => 'success',
            'data' => [
                'balance' => (int) $user->points,
                'history' => $history,
            ],
        ]);
    }

    /**
     * POST /api/v1/points/check
     * Validates how many points can be redeemed for a given order subtotal.
     * Body: { points_to_use: int, order_total: float }
     */
    public function check(Request $request)
    {
        /** @var User $user */
        $user = $request->user();

        $pointsRequested = (int) $request->input('points_to_use', 0);
        $orderTotal      = (float) $request->input('order_total', 0);

        if ($pointsRequested <= 0 || $orderTotal <= 0) {
            return response()->json([
                'status'  => 'error',
                'message' => 'points_to_use and order_total must be greater than 0',
            ], 422);
        }

        $maxByBalance  = (int) $user->points;
        $maxByRule     = (int) floor(($orderTotal * 0.20) * 10);
        $pointsAllowed = min($pointsRequested, $maxByBalance, $maxByRule);
        $discount      = (int) floor($pointsAllowed / 10);

        return response()->json([
            'status' => 'success',
            'data' => [
                'points_requested' => $pointsRequested,
                'points_allowed'   => $pointsAllowed,
                'discount_amount'  => $discount,
                'user_balance'     => $maxByBalance,
                'max_by_rule'      => $maxByRule,
            ],
        ]);
    }
}
