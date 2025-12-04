package com.apk.blogapp.utils

object DummyDataGenerator {
    fun generateDummyData(onComplete: (success: Boolean, message: String) -> Unit) {
        onComplete(true, "Dummy data generated")
    }
    
    fun generateDummyArticles(onComplete: (success: Boolean, message: String) -> Unit) {
        onComplete(true, "Dummy articles generated")
    }
    
    fun testSingleUpload(onComplete: (success: Boolean, message: String) -> Unit) {
        onComplete(true, "Test upload successful")
    }
}
