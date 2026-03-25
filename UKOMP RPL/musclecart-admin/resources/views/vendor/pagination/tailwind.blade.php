@if ($paginator->hasPages())
    <nav role="navigation" aria-label="Pagination Navigation" class="flex items-center justify-between">
        {{-- Mobile Pagination --}}
        <div class="flex gap-2 items-center justify-between sm:hidden w-full">
            @if ($paginator->onFirstPage())
                <span class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-400 bg-white border border-gray-300 cursor-not-allowed rounded-lg font-inter">
                    <i class="fas fa-chevron-left mr-2"></i> Previous
                </span>
            @else
                <a href="{{ $paginator->previousPageUrl() }}" rel="prev" class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors font-inter">
                    <i class="fas fa-chevron-left mr-2"></i> Previous
                </a>
            @endif

            @if ($paginator->hasMorePages())
                <a href="{{ $paginator->nextPageUrl() }}" rel="next" class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors font-inter">
                    Next <i class="fas fa-chevron-right ml-2"></i>
                </a>
            @else
                <span class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-400 bg-white border border-gray-300 cursor-not-allowed rounded-lg font-inter">
                    Next <i class="fas fa-chevron-right ml-2"></i>
                </span>
            @endif
        </div>

        {{-- Desktop Pagination --}}
        <div class="hidden sm:flex sm:items-center sm:justify-between w-full">
            <div>
                <p class="text-sm text-gray-600 font-inter">
                    Showing
                    @if ($paginator->firstItem())
                        <span class="font-semibold text-gray-900">{{ $paginator->firstItem() }}</span>
                        to
                        <span class="font-semibold text-gray-900">{{ $paginator->lastItem() }}</span>
                    @else
                        {{ $paginator->count() }}
                    @endif
                    of
                    <span class="font-semibold text-gray-900">{{ $paginator->total() }}</span>
                    results
                </p>
            </div>

            <div>
                <nav class="inline-flex items-center space-x-2" aria-label="Pagination">
                    {{-- Previous Page Link --}}
                    @if ($paginator->onFirstPage())
                        <span class="p-2 text-gray-400 cursor-not-allowed">
                            <i class="fas fa-chevron-left"></i>
                        </span>
                    @else
                        <a href="{{ $paginator->previousPageUrl() }}" rel="prev" class="p-2 text-gray-600 hover:text-gray-800 transition-colors">
                            <i class="fas fa-chevron-left"></i>
                        </a>
                    @endif

                    {{-- Pagination Elements --}}
                    @foreach ($elements as $element)
                        {{-- "Three Dots" Separator --}}
                        @if (is_string($element))
                            <span class="px-2 text-gray-500 font-inter">{{ $element }}</span>
                        @endif

                        {{-- Array Of Links --}}
                        @if (is_array($element))
                            @foreach ($element as $page => $url)
                                @if ($page == $paginator->currentPage())
                                    <span class="px-3 py-2 bg-blue-600 text-white rounded-lg font-medium font-inter text-sm">{{ $page }}</span>
                                @else
                                    <a href="{{ $url }}" class="px-3 py-2 text-gray-600 hover:bg-gray-100 rounded-lg font-medium font-inter text-sm transition-colors">{{ $page }}</a>
                                @endif
                            @endforeach
                        @endif
                    @endforeach

                    {{-- Next Page Link --}}
                    @if ($paginator->hasMorePages())
                        <a href="{{ $paginator->nextPageUrl() }}" rel="next" class="p-2 text-gray-600 hover:text-gray-800 transition-colors">
                            <i class="fas fa-chevron-right"></i>
                        </a>
                    @else
                        <span class="p-2 text-gray-400 cursor-not-allowed">
                            <i class="fas fa-chevron-right"></i>
                        </span>
                    @endif
                </nav>
            </div>
        </div>
    </nav>
@endif
