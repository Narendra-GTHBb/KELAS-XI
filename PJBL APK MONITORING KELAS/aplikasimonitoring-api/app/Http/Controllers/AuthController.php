<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    /**
     * Login user dan generate token
     */
    public function login(Request $request)
    {
        // Validasi input dasar
        $credentials = $request->validate([
            'username' => 'required|string',
            'password' => 'required|string',
            'role' => 'required|string|in:Siswa,Kurikulum,Kepala Sekolah,Admin',
        ]);

        // Cari user berdasarkan username dan role
        $user = User::where('username', $credentials['username'])
                    ->where('role', $credentials['role'])
                    ->first();

        // Cek apakah user ada
        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'Username atau role tidak ditemukan'
            ], 404);
        }

        // Cek password
        if (!Hash::check($credentials['password'], $user->password)) {
            return response()->json([
                'success' => false,
                'message' => 'Password salah'
            ], 401);
        }

        // Hapus token lama
        $user->tokens()->delete();

        // Buat token baru
        $token = $user->createToken('auth-token')->plainTextToken;

        // Prepare user data
        $userData = [
            'id' => $user->id,
            'name' => $user->nama ?? $user->username,
            'username' => $user->username,
            'role' => $user->role,
        ];

        // Tambahkan kelas_id dan kelas untuk role Siswa
        if ($user->role === 'Siswa' && $user->kelas_id) {
            $userData['kelas_id'] = $user->kelas_id;
            $userData['kelas'] = $user->kelas?->kelas ?? null;
        }

        return response()->json([
            'success' => true,
            'message' => 'Login berhasil',
            'data' => [
                'user' => $userData,
                'token' => $token
            ]
        ], 200);
    }

    /**
     * Logout user
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'success' => true,
            'message' => 'Logout berhasil'
        ], 200);
    }

    /**
     * Get user yang sedang login
     */
    public function me(Request $request)
    {
        $user = $request->user();
        
        $userData = [
            'id' => $user->id,
            'name' => $user->nama ?? $user->username,
            'username' => $user->username,
            'role' => $user->role,
        ];

        // Tambahkan kelas_id dan kelas untuk role Siswa
        if ($user->role === 'Siswa' && $user->kelas_id) {
            $userData['kelas_id'] = $user->kelas_id;
            $userData['kelas'] = $user->kelas?->kelas ?? null;
        }

        return response()->json([
            'success' => true,
            'data' => $userData
        ], 200);
    }
}
