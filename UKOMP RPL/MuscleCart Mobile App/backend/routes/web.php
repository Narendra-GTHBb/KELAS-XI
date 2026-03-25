<?php

use Illuminate\Support\Facades\Route;

// Redirect to admin panel on port 8001
Route::get('/', function () {
    return view('redirect-admin');
});

Route::get('/admin', function () {
    return view('redirect-admin');
});

Route::get('/admin/{any}', function () {
    return view('redirect-admin');
})->where('any', '.*');
