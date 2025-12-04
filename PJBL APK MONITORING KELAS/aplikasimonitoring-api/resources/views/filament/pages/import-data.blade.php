<x-filament-panels::page>
    <div class="max-w-4xl mx-auto space-y-6">
        {{-- Import Type Selection --}}
        <x-filament::section>
            <x-slot name="heading">Pilih Tipe Data</x-slot>
            <select wire:model.live="importType" class="w-full rounded-lg border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white">
                @foreach($this->getImportTypes() as $value => $label)
                    <option value="{{ $value }}">{{ $label }}</option>
                @endforeach
            </select>
        </x-filament::section>

        {{-- File Upload --}}
        <x-filament::section>
            <x-slot name="heading">Upload File CSV</x-slot>
            
            <div 
                x-data="{ isDragging: false }"
                x-on:dragover.prevent="isDragging = true"
                x-on:dragleave.prevent="isDragging = false"
                x-on:drop.prevent="isDragging = false; $refs.fileInput.files = $event.dataTransfer.files; $refs.fileInput.dispatchEvent(new Event('change', { bubbles: true }))"
                :class="{ 'border-primary-500 bg-primary-50': isDragging }"
                class="border-2 border-dashed border-orange-400 rounded-lg p-6 text-center bg-orange-50 dark:bg-orange-900/10"
            >
                {{-- Icon Cloud - ukuran normal --}}
                <div class="mb-3">
                    <x-heroicon-o-cloud-arrow-up class="w-12 h-12 mx-auto text-orange-400" />
                </div>

                {{-- Browse Button --}}
                <label for="csv-file" class="inline-flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg cursor-pointer">
                    <x-heroicon-o-folder-open class="w-4 h-4" />
                    Browse Files
                </label>
                <input 
                    x-ref="fileInput"
                    id="csv-file" 
                    type="file" 
                    wire:model="file" 
                    accept=".csv,.txt"
                    class="hidden"
                >

                <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">Drag and drop files here</p>
                <p class="mt-1 text-xs text-gray-400">Format CSV, maksimal 5MB</p>

                {{-- Loading --}}
                <div wire:loading wire:target="file" class="mt-3">
                    <x-filament::loading-indicator class="w-5 h-5 mx-auto" />
                    <span class="text-sm text-gray-500">Uploading...</span>
                </div>
            </div>

            {{-- File Info --}}
            @if($file)
                <div class="mt-4 p-3 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-300 dark:border-green-700 flex items-center justify-between">
                    <div class="flex items-center gap-3">
                        <x-heroicon-o-document-text class="w-6 h-6 text-green-600" />
                        <div>
                            <p class="font-medium text-gray-900 dark:text-white text-sm">{{ $file->getClientOriginalName() }}</p>
                            <p class="text-xs text-gray-500">{{ number_format($file->getSize() / 1024, 2) }} KB</p>
                        </div>
                    </div>
                    <button type="button" wire:click="resetForm" class="text-red-500 hover:text-red-700">
                        <x-heroicon-o-x-mark class="w-5 h-5" />
                    </button>
                </div>
            @endif
        </x-filament::section>

        {{-- Preview --}}
        @if($showPreview && count($previewHeaders) > 0)
            <x-filament::section>
                <x-slot name="heading">Preview Data ({{ count($previewData) }} baris)</x-slot>
                
                <div class="overflow-x-auto">
                    <table class="w-full text-sm border border-gray-200 dark:border-gray-700">
                        <thead class="bg-gray-100 dark:bg-gray-800">
                            <tr>
                                @foreach($previewHeaders as $header)
                                    <th class="px-3 py-2 text-left font-medium text-gray-700 dark:text-gray-300 border-b">{{ $header }}</th>
                                @endforeach
                            </tr>
                        </thead>
                        <tbody>
                            @foreach($previewData as $row)
                                <tr class="border-b border-gray-100 dark:border-gray-700">
                                    @foreach($row as $cell)
                                        <td class="px-3 py-2 text-gray-900 dark:text-gray-100">{{ $cell }}</td>
                                    @endforeach
                                </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>

                <div class="mt-4 flex justify-end gap-3">
                    <x-filament::button color="gray" wire:click="resetForm">Reset</x-filament::button>
                    <x-filament::button wire:click="import" wire:loading.attr="disabled">
                        <span wire:loading.remove wire:target="import">Import Data</span>
                        <span wire:loading wire:target="import">Importing...</span>
                    </x-filament::button>
                </div>
            </x-filament::section>
        @endif

        {{-- Result --}}
        @if($importResult)
            <x-filament::section>
                <x-slot name="heading">Hasil Import</x-slot>
                
                <div class="grid grid-cols-2 gap-4 mb-4">
                    <div class="p-4 bg-green-100 dark:bg-green-900/30 rounded-lg text-center">
                        <p class="text-2xl font-bold text-green-600">{{ $importResult['success'] }}</p>
                        <p class="text-sm text-green-700 dark:text-green-400">Berhasil</p>
                    </div>
                    <div class="p-4 bg-red-100 dark:bg-red-900/30 rounded-lg text-center">
                        <p class="text-2xl font-bold text-red-600">{{ $importResult['failed'] }}</p>
                        <p class="text-sm text-red-700 dark:text-red-400">Gagal</p>
                    </div>
                </div>

                @if(count($importResult['errors']) > 0)
                    <div class="p-3 bg-red-50 dark:bg-red-900/20 rounded-lg max-h-32 overflow-y-auto">
                        <p class="font-medium text-red-700 dark:text-red-400 text-sm mb-2">Errors:</p>
                        <ul class="text-xs text-red-600 dark:text-red-300 space-y-1">
                            @foreach($importResult['errors'] as $error)
                                <li>• {{ $error }}</li>
                            @endforeach
                        </ul>
                    </div>
                @endif

                <div class="mt-4">
                    <x-filament::button wire:click="resetForm">Import Lagi</x-filament::button>
                </div>
            </x-filament::section>
        @endif

        {{-- Guide --}}
        <x-filament::section collapsible collapsed>
            <x-slot name="heading">Panduan Format CSV</x-slot>
            <div class="text-sm text-gray-600 dark:text-gray-400 space-y-1">
                <p><strong>Guru:</strong> kode_guru, guru, telepon</p>
                <p><strong>Mapel:</strong> kode_mapel, mapel</p>
                <p><strong>Kelas:</strong> kode_kelas, kelas</p>
                <p><strong>Jadwal:</strong> hari, jam_mulai, jam_selesai, guru_mengajar_id</p>
                <p><strong>User:</strong> name, username, email, password, role</p>
                <p><strong>Guru Mengajar:</strong> guru_id, mapel_id, kelas_id</p>
            </div>
        </x-filament::section>
    </div>
</x-filament-panels::page>
