package tf_api.keras.layers

import org.tensorflow.Operand
import org.tensorflow.Shape
import org.tensorflow.op.Ops
import org.tensorflow.op.core.Constant
import tf_api.keras.TensorShape
import kotlin.math.abs


class Flatten<T : Number> : Layer<T>() {
    private lateinit var units: Constant<Int>

    override fun defineVariables(tf: Ops, inputShape: Shape) {
        val tensorShape = TensorShape(inputShape)
        units = tf.constant(intArrayOf(-1, 4*(tensorShape.numElements() / abs(tensorShape.size(0))).toInt()))
    }

    override fun computeOutputShape(inputShape: Shape): Shape {
        // leaves unknown dimensions unknown
        return Shape.make(4 * TensorShape(inputShape).numElements());
    }

    override fun transformInput(tf: Ops, input: Operand<T>): Operand<T> {
       return tf.reshape(input, units)
    }
}