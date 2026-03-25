@extends('admin.layouts.app')

@section('title', 'Order Detail - MuscleCart Admin')
@section('header', 'Order Detail')

@section('content')
<div class="max-w-5xl">
    {{-- Alerts --}}
    @if(session('success'))
        <div class="mb-4 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded flex items-center gap-2">
            <i class="fas fa-check-circle"></i> {{ session('success') }}
        </div>
    @endif
    @if(session('error'))
        <div class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded flex items-center gap-2">
            <i class="fas fa-exclamation-circle"></i> {{ session('error') }}
        </div>
    @endif

    <div class="flex gap-2 mb-4">
        <a href="{{ route('admin.orders.index') }}" class="text-gray-600 hover:text-gray-800">
            <i class="fas fa-arrow-left mr-1"></i> Back to Orders
        </a>
    </div>

    {{-- Quick Action Buttons --}}
    @if(count($allowedTransitions) > 0)
    <div class="bg-white rounded-lg shadow p-4 mb-6">
        <h4 class="text-sm font-semibold text-gray-600 mb-3 uppercase tracking-wide">Quick Actions</h4>
        <div class="flex flex-wrap gap-2">
            @foreach($allowedTransitions as $nextStatus)
                {{-- Paid bukan aksi admin, cukup tampil sebagai info di card payment --}}
                @if($nextStatus === 'paid')
                    @continue
                @endif
                @php
                    $btnColor = match($nextStatus) {
                        'paid'       => 'bg-blue-500 hover:bg-blue-600',
                        'processing' => 'bg-indigo-500 hover:bg-indigo-600',
                        'shipped'    => 'bg-purple-500 hover:bg-purple-600',
                        'delivered'  => 'bg-green-500 hover:bg-green-600',
                        'completed'  => 'bg-emerald-600 hover:bg-emerald-700',
                        'cancelled'  => 'bg-red-500 hover:bg-red-600',
                        default      => 'bg-gray-500 hover:bg-gray-600',
                    };
                    $btnIcon = match($nextStatus) {
                        'paid'       => 'fa-credit-card',
                        'processing' => 'fa-cog',
                        'shipped'    => 'fa-truck',
                        'delivered'  => 'fa-box-open',
                        'completed'  => 'fa-check-double',
                        'cancelled'  => 'fa-times-circle',
                        default      => 'fa-arrow-right',
                    };
                @endphp
                <form action="{{ route('admin.orders.update', $order) }}" method="POST" class="inline"
                      onsubmit="return confirm('Mark order as {{ ucfirst($nextStatus) }}?')">
                    @csrf
                    @method('PUT')
                    <input type="hidden" name="status" value="{{ $nextStatus }}">
                    <button type="submit" class="{{ $btnColor }} text-white px-4 py-2 rounded-lg text-sm font-medium transition flex items-center gap-1">
                        <i class="fas {{ $btnIcon }}"></i> Mark as {{ ucfirst($nextStatus) }}
                    </button>
                </form>
            @endforeach
        </div>
    </div>
    @endif

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {{-- Left Column --}}
        <div class="lg:col-span-2 space-y-6">
            {{-- Order Items --}}
            <div class="bg-white rounded-lg shadow">
                <div class="p-6 border-b">
                    <div class="flex items-center justify-between">
                        <h3 class="text-lg font-semibold text-gray-800">Order #{{ $order->order_number ?? $order->id }}</h3>
                        <span class="inline-block px-3 py-1 text-sm rounded font-medium
                            @if($order->status == 'pending')    bg-yellow-100 text-yellow-800
                            @elseif($order->status == 'paid')   bg-blue-100 text-blue-800
                            @elseif($order->status == 'processing') bg-indigo-100 text-indigo-800
                            @elseif($order->status == 'shipped')    bg-purple-100 text-purple-800
                            @elseif($order->status == 'delivered')  bg-green-100 text-green-800
                            @elseif($order->status == 'completed')  bg-emerald-100 text-emerald-800
                            @elseif($order->status == 'cancelled')  bg-red-100 text-red-800
                            @else bg-gray-100 text-gray-800 @endif">
                            {{ ucfirst($order->status) }}
                        </span>
                    </div>
                </div>
                <div class="p-6">
                    <h4 class="font-medium text-gray-700 mb-3">Order Items</h4>
                    <table class="w-full">
                        <thead>
                            <tr class="text-left text-gray-600 border-b text-sm">
                                <th class="pb-2">Product</th>
                                <th class="pb-2">Price</th>
                                <th class="pb-2">Qty</th>
                                <th class="pb-2 text-right">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            @forelse($order->orderItems as $item)
                            <tr class="border-b text-sm">
                                <td class="py-3">{{ $item->product->name ?? $item->product_name ?? 'Deleted Product' }}</td>
                                <td class="py-3">Rp {{ number_format($item->price, 0, ',', '.') }}</td>
                                <td class="py-3">{{ $item->quantity }}</td>
                                <td class="py-3 text-right">Rp {{ number_format($item->price * $item->quantity, 0, ',', '.') }}</td>
                            </tr>
                            @empty
                            <tr>
                                <td colspan="4" class="py-4 text-center text-gray-500">No items</td>
                            </tr>
                            @endforelse
                        </tbody>
                        <tfoot>
                            <tr class="font-semibold">
                                <td colspan="3" class="pt-4 text-right">Total:</td>
                                <td class="pt-4 text-right text-lg text-orange-600">Rp {{ number_format($order->total_amount, 0, ',', '.') }}</td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>

            {{-- Status History Timeline --}}
            @if($order->statusHistories->isNotEmpty())
            <div class="bg-white rounded-lg shadow p-6">
                <h4 class="font-medium text-gray-700 mb-4">Status History</h4>
                <div class="relative">
                    @foreach($order->statusHistories as $history)
                    <div class="flex gap-4 mb-4 last:mb-0">
                        <div class="flex flex-col items-center">
                            <div class="w-3 h-3 rounded-full mt-1 flex-shrink-0
                                @if($history->status == 'completed') bg-emerald-500
                                @elseif($history->status == 'cancelled') bg-red-500
                                @elseif($history->status == 'shipped') bg-purple-500
                                @else bg-orange-400 @endif">
                            </div>
                            @if(!$loop->last)
                            <div class="w-0.5 bg-gray-200 flex-1 mt-1"></div>
                            @endif
                        </div>
                        <div class="pb-4">
                            <div class="flex items-center gap-2">
                                <span class="font-medium text-sm text-gray-800 capitalize">{{ $history->status }}</span>
                                @if($history->previous_status)
                                <span class="text-gray-400 text-xs">← {{ $history->previous_status }}</span>
                                @endif
                            </div>
                            @if($history->note)
                            <p class="text-xs text-gray-500 mt-0.5">{{ $history->note }}</p>
                            @endif
                            <p class="text-xs text-gray-400 mt-0.5">
                                {{ $history->created_at ? $history->created_at->format('d M Y H:i') : '-' }}
                                @if($history->changed_by_role) · {{ ucfirst($history->changed_by_role) }} @endif
                            </p>
                        </div>
                    </div>
                    @endforeach
                </div>
            </div>
            @endif

            @if($order->notes)
            <div class="bg-white rounded-lg shadow p-6">
                <h4 class="font-medium text-gray-700 mb-2">Notes</h4>
                <p class="text-gray-600">{{ $order->notes }}</p>
            </div>
            @endif
        </div>

        {{-- Right Sidebar --}}
        <div class="space-y-6">
            {{-- Customer --}}
            <div class="bg-white rounded-lg shadow p-6">
                <h4 class="font-medium text-gray-700 mb-3">Customer</h4>
                <p class="font-medium">{{ $order->user->name ?? 'Guest' }}</p>
                <p class="text-sm text-gray-500">{{ $order->user->email ?? '-' }}</p>
                <p class="text-sm text-gray-500">{{ $order->user->phone ?? '-' }}</p>
            </div>

            {{-- Shipping Address --}}
            <div class="bg-white rounded-lg shadow p-6">
                <h4 class="font-medium text-gray-700 mb-3">Shipping Address</h4>
                <p class="text-sm text-gray-600">
                    @if(is_array($order->shipping_address) || is_object($order->shipping_address))
                        @php $addr = (array) $order->shipping_address; @endphp
                        {{ $addr['address'] ?? $addr['street'] ?? '-' }}<br>
                        @if(!empty($addr['city'])) {{ $addr['city'] }}@endif
                        @if(!empty($addr['postal_code'])) &nbsp;{{ $addr['postal_code'] }}@endif
                        @if(!empty($addr['phone'])) <br>{{ $addr['phone'] }}@endif
                    @else
                        {{ $order->shipping_address ?? '-' }}
                    @endif
                </p>
            </div>

            {{-- Payment --}}
            <div class="bg-white rounded-lg shadow p-6">
                <h4 class="font-medium text-gray-700 mb-3">Pembayaran</h4>
                <div class="space-y-2">
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-500">Metode</span>
                        <span class="text-sm font-semibold capitalize">
                            @if(in_array($order->payment_method, ['cod', 'cash']))
                                <span class="inline-flex items-center gap-1">
                                    <i class="fas fa-money-bill-wave text-green-500"></i>
                                    COD / Tunai
                                </span>
                            @elseif(in_array($order->payment_method, ['transfer', 'bank_transfer']))
                                <span class="inline-flex items-center gap-1">
                                    <i class="fas fa-university text-blue-500"></i>
                                    Transfer Bank
                                </span>
                            @elseif(in_array($order->payment_method, ['ewallet', 'e_wallet', 'gopay', 'ovo', 'dana']))
                                <span class="inline-flex items-center gap-1">
                                    <i class="fas fa-mobile-alt text-purple-500"></i>
                                    E-Wallet
                                </span>
                            @else
                                {{ ucfirst(str_replace('_', ' ', $order->payment_method ?? '-')) }}
                            @endif
                        </span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-500">Status Bayar</span>
                        @if($order->payment_status === 'paid' || $order->status === 'paid')
                            <span class="inline-flex items-center gap-1 text-xs font-semibold text-green-700 bg-green-100 px-2 py-1 rounded-full">
                                <i class="fas fa-check-circle"></i> Sudah Dibayar
                            </span>
                        @elseif($order->payment_status === 'failed')
                            <span class="inline-flex items-center gap-1 text-xs font-semibold text-red-700 bg-red-100 px-2 py-1 rounded-full">
                                <i class="fas fa-times-circle"></i> Gagal
                            </span>
                        @elseif(in_array($order->payment_method, ['cod', 'cash']))
                            <span class="inline-flex items-center gap-1 text-xs font-semibold text-yellow-700 bg-yellow-100 px-2 py-1 rounded-full">
                                <i class="fas fa-clock"></i> Bayar saat terima
                            </span>
                        @else
                            <span class="inline-flex items-center gap-1 text-xs font-semibold text-yellow-700 bg-yellow-100 px-2 py-1 rounded-full">
                                <i class="fas fa-clock"></i> Belum Dibayar
                            </span>
                        @endif
                    </div>
                    @if($order->paid_at)
                    <p class="text-xs text-gray-400 pt-1">Dibayar: {{ $order->paid_at->format('d M Y H:i') }}</p>
                    @endif
                </div>
            </div>

            {{-- Tracking Info --}}
            @if($order->tracking_number || $order->courier)
            <div class="bg-white rounded-lg shadow p-6">
                <h4 class="font-medium text-gray-700 mb-3">Tracking</h4>
                <p class="text-sm">Courier: <span class="font-medium">{{ $order->courier ?? '-' }}</span></p>
                <p class="text-sm">No. Resi: <span class="font-mono font-medium text-indigo-600">{{ $order->tracking_number ?? '-' }}</span></p>
            </div>
            @endif

            {{-- Tracking Form --}}
            @php
                $trackingLocked = in_array($order->status, ['shipped', 'delivered', 'completed', 'cancelled']);
            @endphp
            <div class="bg-white rounded-lg shadow p-6">
                <div class="flex items-center justify-between mb-1">
                    <h4 class="font-medium text-gray-700">Tracking & Kurir</h4>
                    @if($trackingLocked)
                        <span class="text-xs px-2 py-1 rounded-full font-medium
                            {{ $order->status === 'cancelled' ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600' }}">
                            <i class="fas fa-lock mr-1"></i>
                            {{ $order->status === 'cancelled' ? 'Dibatalkan' : 'Final' }}
                        </span>
                    @endif
                </div>
                <p class="text-xs text-gray-400 mb-3">Ubah status via tombol Quick Actions di atas.</p>

                @if($trackingLocked)
                    {{-- READ-ONLY view when shipped/delivered/completed/cancelled --}}
                    <div class="space-y-3 bg-gray-50 rounded-lg p-3 border border-gray-200">
                        <div>
                            <p class="text-xs text-gray-400 mb-0.5">Courier</p>
                            <p class="text-sm font-medium text-gray-800">{{ $order->courier ?: '-' }}</p>
                        </div>
                        <div>
                            <p class="text-xs text-gray-400 mb-0.5">No. Resi</p>
                            <p class="text-sm font-mono font-semibold text-indigo-600">{{ $order->tracking_number ?: '-' }}</p>
                        </div>
                        @if($order->notes)
                        <div>
                            <p class="text-xs text-gray-400 mb-0.5">Note</p>
                            <p class="text-sm text-gray-600">{{ $order->notes }}</p>
                        </div>
                        @endif
                    </div>
                    @if(!in_array($order->status, ['completed', 'cancelled']))
                    <p class="text-xs text-gray-400 mt-2 text-center">
                        <i class="fas fa-info-circle mr-1"></i>
                        Data tracking terkunci karena pesanan sudah dikirim. Hubungi admin jika perlu koreksi.
                    </p>
                    @endif
                @else
                    {{-- EDITABLE form when status is pending/paid/processing --}}
                    <form action="{{ route('admin.orders.update', $order) }}" method="POST" class="space-y-3" id="trackingForm">
                        @csrf
                        @method('PUT')
                        <input type="hidden" name="status" value="{{ $order->status }}">

                        <div>
                            <label class="block text-xs text-gray-500 mb-1">Courier (JNE, J&T, etc.)</label>
                            <input type="text" name="courier" id="courierInput" value="{{ $order->courier }}"
                                placeholder="e.g. JNE Reguler"
                                class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                        </div>

                        <div>
                            <label class="block text-xs text-gray-500 mb-1">No. Resi / Tracking Number</label>
                            <div class="flex gap-2">
                                <input type="text" name="tracking_number" id="trackingInput" value="{{ $order->tracking_number }}"
                                    placeholder="e.g. JNE1234567890"
                                    class="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm font-mono focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                                <button type="button" onclick="generateResi()"
                                    class="bg-indigo-500 hover:bg-indigo-600 text-white px-3 py-2 rounded-lg text-xs font-medium whitespace-nowrap transition"
                                    title="Generate otomatis">
                                    <i class="fas fa-magic mr-1"></i> Generate
                                </button>
                            </div>
                            <p class="text-xs text-gray-400 mt-1">
                                <i class="fas fa-info-circle mr-1"></i>
                                Klik <strong>Generate</strong> untuk buat nomor resi otomatis, atau isi manual.
                            </p>
                        </div>

                        <div>
                            <label class="block text-xs text-gray-500 mb-1">Note (optional)</label>
                            <textarea name="notes" rows="2" placeholder="Internal note..."
                                class="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-orange-500 focus:border-transparent">{{ $order->notes }}</textarea>
                        </div>

                        <button type="submit" class="w-full bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-lg transition text-sm font-medium">
                            <i class="fas fa-save mr-1"></i> Save Changes
                        </button>
                    </form>

                    <script>
                    function generateResi() {
                        const courierInput = document.getElementById('courierInput');
                        const trackingInput = document.getElementById('trackingInput');
                        const courier = courierInput.value.trim().toUpperCase();

                        // Determine prefix from courier name
                        let prefix = 'MC';
                        const lc = courier.toLowerCase();
                        if (lc.includes('jne'))       prefix = 'JNE';
                        else if (lc.includes('jnt') || lc.includes('j&t')) prefix = 'JNT';
                        else if (lc.includes('tiki'))  prefix = 'TIKI';
                        else if (lc.includes('pos'))   prefix = 'POS';
                        else if (lc.includes('sicepat')) prefix = 'SCP';
                        else if (lc.includes('anteraja')) prefix = 'ANR';
                        else if (lc.includes('ninja')) prefix = 'NIN';
                        else if (lc.includes('gosend') || lc.includes('gojek')) prefix = 'GSN';
                        else if (courier.length > 0)   prefix = courier.slice(0, 3).replace(/\s/g, '');

                        // Generate: PREFIX + YYYYMMDD + 8 random digits
                        const now = new Date();
                        const datePart = now.getFullYear().toString()
                            + String(now.getMonth() + 1).padStart(2, '0')
                            + String(now.getDate()).padStart(2, '0');
                        const randomPart = Math.floor(10000000 + Math.random() * 90000000).toString();
                        const resi = prefix + datePart + randomPart;

                        trackingInput.value = resi;
                        trackingInput.focus();
                        trackingInput.select();
                    }
                    </script>
                @endif
            </div>

            {{-- Timestamps --}}
            <div class="bg-white rounded-lg shadow p-6 text-xs text-gray-400 space-y-1">
                <p>Created: {{ $order->created_at ? $order->created_at->format('d M Y H:i') : '-' }}</p>
                <p>Updated: {{ $order->updated_at ? $order->updated_at->format('d M Y H:i') : '-' }}</p>
                @if($order->shipped_at)   <p>Shipped: {{ $order->shipped_at->format('d M Y H:i') }}</p>   @endif
                @if($order->delivered_at) <p>Delivered: {{ $order->delivered_at->format('d M Y H:i') }}</p> @endif
                @if($order->completed_at) <p>Completed: {{ $order->completed_at->format('d M Y H:i') }}</p> @endif
            </div>
        </div>
    </div>
</div>
@endsection

