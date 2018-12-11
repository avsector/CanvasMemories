package com.samsaz.canvasmemories.model

/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */
sealed class Screen {
    object Editor: Screen()
    object Stats: Screen()
}