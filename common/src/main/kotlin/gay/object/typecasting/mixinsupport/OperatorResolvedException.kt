package gay.`object`.typecasting.mixinsupport

import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator

data class OperatorResolvedException(val operator: Operator) : RuntimeException()
