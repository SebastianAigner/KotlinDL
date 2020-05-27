package examples.production

import examples.keras.mnist.util.NUM_LABELS
import tf_api.keras.Sequential
import tf_api.keras.activations.Activations
import tf_api.keras.dataset.ImageDataset
import tf_api.keras.initializers.Xavier
import tf_api.keras.layers.Dense
import tf_api.keras.layers.Flatten
import tf_api.keras.layers.Input
import tf_api.keras.layers.twodim.Conv2D
import tf_api.keras.layers.twodim.ConvPadding
import tf_api.keras.layers.twodim.MaxPool2D

private const val NUM_CHANNELS = 1L
private const val IMAGE_SIZE = 28L
private const val SEED = 12L

val lenet5 = Sequential.of<Float>(
    Input(
        IMAGE_SIZE,
        IMAGE_SIZE,
        NUM_CHANNELS
    ),
    Conv2D(
        filters = 32,
        kernelSize = longArrayOf(5, 5),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = Xavier(SEED),
        biasInitializer = Xavier(SEED),
        padding = ConvPadding.SAME,
        name = "1"
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1)
    ),
    Conv2D(
        filters = 64,
        kernelSize = longArrayOf(5, 5),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = Xavier(SEED),
        biasInitializer = Xavier(SEED),
        padding = ConvPadding.SAME,
        name = "2"
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1)
    ),
    Flatten(), // 3136
    Dense(
        outputSize = 120,
        activation = Activations.Relu,
        kernelInitializer = Xavier(SEED),
        biasInitializer = Xavier(SEED),
        name = "3"
    ),
    Dense(
        outputSize = 84,
        activation = Activations.Relu,
        kernelInitializer = Xavier(SEED),
        biasInitializer = Xavier(SEED),
        name = "4"
    ),
    Dense(
        outputSize = NUM_LABELS,
        activation = Activations.Linear,
        kernelInitializer = Xavier(SEED),
        biasInitializer = Xavier(SEED),
        name = "5"
    )
)

fun getLabel(dataset: ImageDataset, imageId: Int): Int {
    val trainImageLabel = dataset.getImageLabel(imageId)
    return trainImageLabel.indexOf(trainImageLabel.max()!!)
}