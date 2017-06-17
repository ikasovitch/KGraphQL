package com.github.pgutkowski.kgraphql.schema.scalar

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.github.pgutkowski.kgraphql.ExecutionException
import com.github.pgutkowski.kgraphql.RequestException
import com.github.pgutkowski.kgraphql.schema.builtin.*
import com.github.pgutkowski.kgraphql.schema.model.KQLType

@Suppress("UNCHECKED_CAST")
fun <T : Any> deserializeScalar(scalar: KQLType.Scalar<T>, value : String): T {
    try {
        return when(scalar.coercion){
            //built in scalars
            STRING_COERCION -> STRING_COERCION.deserialize(value) as T
            FLOAT_COERCION -> FLOAT_COERCION.deserialize(value) as T
            DOUBLE_COERCION -> DOUBLE_COERCION.deserialize(value) as T
            INT_COERCION -> INT_COERCION.deserialize(value) as T
            BOOLEAN_COERCION -> BOOLEAN_COERCION.deserialize(value) as T

            is StringScalarCoercion<T> -> scalar.coercion.deserialize(STRING_COERCION.deserialize(value))
            is IntScalarCoercion<T> -> scalar.coercion.deserialize(INT_COERCION.deserialize(value))
            is DoubleScalarCoercion<T> -> scalar.coercion.deserialize(DOUBLE_COERCION.deserialize(value))
            is BooleanScalarCoercion<T> -> scalar.coercion.deserialize(BOOLEAN_COERCION.deserialize(value))
            else -> throw ExecutionException("Unsupported coercion for scalar type ${scalar.name}")
        }
    } catch (e: Exception){
        throw RequestException("argument '$value' is not valid value of type ${scalar.name}", e)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> serializeScalar(jsonNodeFactory: JsonNodeFactory, scalar: KQLType.Scalar<*>, value: T) : JsonNode {
    return when(scalar.coercion){
        is StringScalarCoercion<*> -> {
            jsonNodeFactory.textNode((scalar.coercion as StringScalarCoercion<T>).serialize(value))
        }
        is IntScalarCoercion<*> -> {
            jsonNodeFactory.numberNode((scalar.coercion as IntScalarCoercion<T>).serialize(value))
        }
        is DoubleScalarCoercion<*> -> {
            jsonNodeFactory.numberNode((scalar.coercion as DoubleScalarCoercion<T>).serialize(value))
        }
        is BooleanScalarCoercion<*> -> {
            jsonNodeFactory.booleanNode((scalar.coercion as BooleanScalarCoercion<T>).serialize(value))
        }
        else -> throw ExecutionException("Unsupported coercion for scalar type ${scalar.name}")
    }
}

