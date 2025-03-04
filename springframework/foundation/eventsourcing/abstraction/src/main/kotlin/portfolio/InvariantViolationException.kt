package portfolio

data class InvariantViolationException(val error: ErrorDetail) : RuntimeException(error.code)

