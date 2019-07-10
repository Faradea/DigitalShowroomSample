package ru.macgavrina.digitalshowroom.support

import ru.macgavrina.digitalshowroom.model.*

class PaginatorCalculator {
    companion object {
        fun generatePageNumbersList(totalPages: Int, currentPageNumber: Int): MutableList<PageInPaginator> {

            val pageNumbers = mutableListOf<PageInPaginator>()

            if (currentPageNumber > 1) {
                pageNumbers.add(PageInPaginator("<", PAGINATOR_TYPES_PREVIOUS, false))
            }

            when {
                totalPages <= 5 -> {
                    for (i in 1..totalPages) {
                        pageNumbers.add(PageInPaginator(i.toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == i))
                    }
                }
                (totalPages > 5 && currentPageNumber <= 2) -> {
                    for (i in 1..3) {
                        pageNumbers.add(PageInPaginator(i.toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == i))
                    }
                    pageNumbers.add(PageInPaginator("...", PAGINATOR_TYPES_DIVIDER, false))
                    pageNumbers.add(PageInPaginator(totalPages.toString(), PAGINATOR_TYPES_REALPAGE, false))
                }
                (totalPages > 5 && currentPageNumber == 3) -> {
                    for (i in 1..4) {
                        pageNumbers.add(PageInPaginator(i.toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == i))
                    }
                    pageNumbers.add(PageInPaginator("...", PAGINATOR_TYPES_DIVIDER, false))
                    pageNumbers.add(PageInPaginator(totalPages.toString(), PAGINATOR_TYPES_REALPAGE, false))
                }
                (totalPages > 5 && currentPageNumber > 3 && (totalPages - currentPageNumber) > 2) -> {
                    pageNumbers.add(PageInPaginator("1", PAGINATOR_TYPES_REALPAGE, false))
                    pageNumbers.add(PageInPaginator("...", PAGINATOR_TYPES_DIVIDER, false))
                    pageNumbers.add(PageInPaginator((currentPageNumber-1).toString(), PAGINATOR_TYPES_REALPAGE, false))
                    pageNumbers.add(PageInPaginator(currentPageNumber.toString(), PAGINATOR_TYPES_REALPAGE, true))
                    pageNumbers.add(PageInPaginator((currentPageNumber+1).toString(), PAGINATOR_TYPES_REALPAGE, false))
                    pageNumbers.add(PageInPaginator("...", PAGINATOR_TYPES_DIVIDER, false))
                    pageNumbers.add(PageInPaginator(totalPages.toString(), PAGINATOR_TYPES_REALPAGE, false))
                }
                (totalPages > 5 && (totalPages - currentPageNumber) <= 1) -> {
                    pageNumbers.add(PageInPaginator("1", PAGINATOR_TYPES_REALPAGE, false))
                    pageNumbers.add(PageInPaginator("...", PAGINATOR_TYPES_DIVIDER, false))
                    pageNumbers.add(PageInPaginator((totalPages-2).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages-2))
                    pageNumbers.add(PageInPaginator((totalPages-1).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages-1))
                    pageNumbers.add(PageInPaginator((totalPages).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages))
                }
                (totalPages > 5 && (totalPages - currentPageNumber) == 2) -> {
                    pageNumbers.add(PageInPaginator("1", PAGINATOR_TYPES_REALPAGE, false))
                    pageNumbers.add(PageInPaginator("...", PAGINATOR_TYPES_DIVIDER, false))
                    pageNumbers.add(PageInPaginator((totalPages-3).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages-3))
                    pageNumbers.add(PageInPaginator((totalPages-2).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages-2))
                    pageNumbers.add(PageInPaginator((totalPages-1).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages-1))
                    pageNumbers.add(PageInPaginator((totalPages).toString(), PAGINATOR_TYPES_REALPAGE, currentPageNumber == totalPages))

                }
            }

            if (currentPageNumber < totalPages) {
                pageNumbers.add(PageInPaginator(">", PAGINATOR_TYPES_NEXT, false))
            }

            return pageNumbers
        }
    }
}