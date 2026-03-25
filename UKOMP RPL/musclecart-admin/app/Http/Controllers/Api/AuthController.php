<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;

class AuthController extends Controller
{
    /**
     * Register new user
     */
    public function register(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8|confirmed',
            'phone' => 'nullable|string|max:20',
            'address' => 'nullable|string|max:500',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        try {
            $user = User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),
                'phone' => $request->phone,
                'address' => $request->address,
                'role' => 'customer', // Default role for mobile users
                'is_active' => true,
            ]);

            // Create token for the user
            $token = $user->createToken('mobile-app')->plainTextToken;

            return response()->json([
                'status' => 'success',
                'message' => 'User registered successfully',
                'data' => [
                    'user' => [
                        'id' => $user->id,
                        'name' => $user->name,
                        'email' => $user->email,
                        'phone' => $user->phone,
                        'address' => $user->address,
                        'role' => $user->role,
                    ],
                    'token' => $token,
                ]
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Registration failed',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Login user
     */
    public function login(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required|string|email',
            'password' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        if (!Auth::attempt($request->only('email', 'password'))) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid credentials'
            ], 401);
        }

        $user = Auth::user();

        // Check if user is active
        if (!$user->is_active) {
            return response()->json([
                'status' => 'error',
                'message' => 'Account is suspended. Please contact support.'
            ], 403);
        }

        // Create token for the user
        $token = $user->createToken('mobile-app')->plainTextToken;

        return response()->json([
            'status' => 'success',
            'message' => 'Login successful',
            'data' => [
                'user' => [
                    'id' => $user->id,
                    'name' => $user->name,
                    'email' => $user->email,
                    'phone' => $user->phone,
                    'address' => $user->address,
                    'role' => $user->role,
                ],
                'token' => $token,
            ]
        ]);
    }

    /**
     * Get authenticated user profile
     */
    public function user(Request $request)
    {
        $user = $request->user();

        return response()->json([
            'status' => 'success',
            'data' => [
                'user' => [
                    'id'          => $user->id,
                    'name'        => $user->name,
                    'email'       => $user->email,
                    'phone'       => $user->phone,
                    'address'     => $user->address,
                    'city'        => $user->city,
                    'postal_code' => $user->postal_code,
                    'province_id' => $user->province_id,
                    'city_id'     => $user->city_id,
                    'avatar'      => $user->avatar,
                    'role'        => $user->role,
                    'points'      => (int) $user->points,
                    'created_at'  => $user->created_at,
                    'updated_at'  => $user->updated_at,
                ]
            ]
        ]);
    }

    /**
     * Update user profile
     */
    public function updateProfile(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'sometimes|required|string|max:255',
            'phone' => 'nullable|string|max:20',
            'address' => 'nullable|string|max:500',
            'password' => 'nullable|string|min:8|confirmed',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        try {
            $user = $request->user();

            $updateData = [];
            if ($request->has('name')) {
                $updateData['name'] = $request->name;
            }
            if ($request->has('phone')) {
                $updateData['phone'] = $request->phone;
            }
            if ($request->has('address')) {
                $updateData['address'] = $request->address;
            }
            if ($request->has('city')) {
                $updateData['city'] = $request->city;
            }
            if ($request->has('postal_code')) {
                $updateData['postal_code'] = $request->postal_code;
            }
            if ($request->has('province_id')) {
                $updateData['province_id'] = $request->province_id;
            }
            if ($request->has('city_id')) {
                $updateData['city_id'] = $request->city_id;
            }
            if ($request->has('password') && $request->password) {
                $updateData['password'] = Hash::make($request->password);
            }

            $user->update($updateData);

            return response()->json([
                'status' => 'success',
                'message' => 'Profile updated successfully',
                'data' => [
                    'user' => [
                        'id'          => $user->id,
                        'name'        => $user->name,
                        'email'       => $user->email,
                        'phone'       => $user->phone,
                        'address'     => $user->address,
                        'city'        => $user->city,
                        'postal_code' => $user->postal_code,
                        'province_id' => $user->province_id,
                        'city_id'     => $user->city_id,
                        'avatar'      => $user->avatar,
                        'role'        => $user->role,
                        'points'      => (int) $user->points,
                    ]
                ]
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Profile update failed',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Logout user (revoke token)
     */
    public function logout(Request $request)
    {
        try {
            // Revoke the current token
            $request->user()->currentAccessToken()->delete();

            return response()->json([
                'status' => 'success',
                'message' => 'Logged out successfully'
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Logout failed',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Login or auto-register via Google ID Token.
     * Android sends the Google ID token; we verify it with Google and create/login the user.
     */
    public function loginWithGoogle(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'id_token' => 'required|string',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        // Verify the Google ID token with Google's tokeninfo endpoint
        $tokenInfo = Http::get('https://oauth2.googleapis.com/tokeninfo', [
            'id_token' => $request->id_token,
        ]);

        if (!$tokenInfo->successful() || !isset($tokenInfo['sub'])) {
            return response()->json([
                'status' => 'error',
                'message' => 'Token Google tidak valid'
            ], 401);
        }

        $googleId = $tokenInfo['sub'];
        $email    = $tokenInfo['email'] ?? null;
        $name     = $tokenInfo['name']    ?? ($email ? explode('@', $email)[0] : 'Google User');
        $avatar   = $tokenInfo['picture'] ?? null;

        if (!$email) {
            return response()->json([
                'status' => 'error',
                'message' => 'Email tidak tersedia dari akun Google'
            ], 422);
        }

        // Find user by google_id first, then by email
        $user = User::where('google_id', $googleId)->first()
            ?? User::where('email', $email)->first();

        if (!$user) {
            // Auto-register new Google user
            $user = User::create([
                'name'      => $name,
                'email'     => $email,
                'google_id' => $googleId,
                'avatar'    => $avatar,
                'password'  => Hash::make(Str::random(32)),
                'role'      => 'customer',
                'is_active' => true,
            ]);
        } else {
            // Link google_id if the user registered via email before
            if (!$user->google_id) {
                $user->update(['google_id' => $googleId]);
            }
            // Update avatar if not set
            if (!$user->avatar && $avatar) {
                $user->update(['avatar' => $avatar]);
            }
        }

        if (!$user->is_active) {
            return response()->json([
                'status' => 'error',
                'message' => 'Akun dinonaktifkan. Hubungi support.'
            ], 403);
        }

        $token = $user->createToken('mobile-app')->plainTextToken;

        return response()->json([
            'status'  => 'success',
            'message' => 'Login berhasil',
            'data'    => [
                'user' => [
                    'id'      => $user->id,
                    'name'    => $user->name,
                    'email'   => $user->email,
                    'phone'   => $user->phone,
                    'address' => $user->address,
                    'avatar'  => $user->avatar,
                    'role'    => $user->role,
                    'points'  => (int) $user->points,
                ],
                'token' => $token,
            ]
        ]);
    }

    /**
     * Store / update the FCM push notification token for the authenticated user.
     */
    public function registerFcmToken(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'fcm_token' => 'required|string|max:500',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Validation failed',
                'errors'  => $validator->errors()
            ], 422);
        }

        $request->user()->update(['fcm_token' => $request->fcm_token]);

        return response()->json([
            'status'  => 'success',
            'message' => 'FCM token berhasil disimpan'
        ]);
    }
}
