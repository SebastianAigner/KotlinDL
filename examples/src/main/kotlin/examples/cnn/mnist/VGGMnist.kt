/*
 * Copyright 2020 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package examples.cnn.mnist

import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.activation.Activations
import org.jetbrains.kotlinx.dl.api.core.initializer.HeNormal
import org.jetbrains.kotlinx.dl.api.core.layer.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.Flatten
import org.jetbrains.kotlinx.dl.api.core.layer.Input
import org.jetbrains.kotlinx.dl.api.core.layer.twodim.Conv2D
import org.jetbrains.kotlinx.dl.api.core.layer.twodim.ConvPadding
import org.jetbrains.kotlinx.dl.api.core.layer.twodim.MaxPool2D
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.datasets.Dataset
import org.jetbrains.kotlinx.dl.datasets.handlers.*

private const val EPOCHS = 5
private const val TRAINING_BATCH_SIZE = 200
private const val TEST_BATCH_SIZE = 1000
private const val NUM_LABELS = 10
private const val NUM_CHANNELS = 1L
private const val IMAGE_SIZE = 28L
private const val SEED = 12L

val heNormal = HeNormal(SEED)

/**
 * Kotlin implementation of VGG'11 on Keras with minor changes of number of neurons to be successfully applied to the MNIST dataset.
 */
private val vgg11 = Sequential.of(
    Input(
        IMAGE_SIZE,
        IMAGE_SIZE,
        NUM_CHANNELS
    ),
    Conv2D(
        filters = 32,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 64,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 128,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 128,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 256,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 256,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 128,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    Conv2D(
        filters = 128,
        kernelSize = longArrayOf(3, 3),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal,
        padding = ConvPadding.SAME
    ),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.SAME
    ),
    Flatten(),
    Dense(
        outputSize = 2048,
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal
    ),
    Dense(
        outputSize = 1000,
        activation = Activations.Relu,
        kernelInitializer = heNormal,
        biasInitializer = heNormal
    ),
    Dense(
        outputSize = NUM_LABELS,
        activation = Activations.Linear,
        kernelInitializer = heNormal,
        biasInitializer = heNormal
    )
)

fun main() {
    val (train, test) = Dataset.createTrainAndTestDatasets(
        TRAIN_IMAGES_ARCHIVE,
        TRAIN_LABELS_ARCHIVE,
        TEST_IMAGES_ARCHIVE,
        TEST_LABELS_ARCHIVE,
        NUM_LABELS,
        ::extractImages,
        ::extractLabels
    )

    vgg11.use {
        it.compile(optimizer = Adam(), loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS, metric = Metrics.ACCURACY)

        it.summary()

        it.fit(dataset = train, epochs = EPOCHS, batchSize = TRAINING_BATCH_SIZE)

        val accuracy = it.evaluate(dataset = test, batchSize = TEST_BATCH_SIZE).metrics[Metrics.ACCURACY]

        println("Accuracy: $accuracy")
    }
}
