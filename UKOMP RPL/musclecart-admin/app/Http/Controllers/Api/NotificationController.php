<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    /** GET /api/v1/notifications */
    public function index(Request $request)
    {
        $user   = $request->user();
        $items  = Notification::where('user_id', $user->id)
            ->orderByDesc('created_at')
            ->take(50)
            ->get()
            ->map(fn($n) => [
                'id'             => $n->id,
                'title'          => $n->title,
                'body'           => $n->body,
                'type'           => $n->type,
                'reference_id'   => $n->reference_id,
                'reference_type' => $n->reference_type,
                'is_read'        => $n->read_at !== null,
                'created_at'     => $n->created_at?->toIso8601String(),
            ]);

        $unread = Notification::where('user_id', $user->id)->whereNull('read_at')->count();

        return response()->json([
            'status' => 'success',
            'data'   => [
                'notifications' => $items,
                'unread_count'  => $unread,
            ],
        ]);
    }

    /** POST /api/v1/notifications/read-all */
    public function readAll(Request $request)
    {
        Notification::where('user_id', $request->user()->id)
            ->whereNull('read_at')
            ->update(['read_at' => now()]);

        return response()->json(['status' => 'success', 'message' => 'All notifications marked as read']);
    }

    /** PUT /api/v1/notifications/{id}/read */
    public function read(Request $request, int $id)
    {
        $notification = Notification::where('user_id', $request->user()->id)->findOrFail($id);
        $notification->update(['read_at' => now()]);
        return response()->json(['status' => 'success']);
    }

    /** GET /api/v1/notifications/unread-count */
    public function unreadCount(Request $request)
    {
        $count = Notification::where('user_id', $request->user()->id)->whereNull('read_at')->count();
        return response()->json(['status' => 'success', 'data' => ['unread_count' => $count]]);
    }
}
