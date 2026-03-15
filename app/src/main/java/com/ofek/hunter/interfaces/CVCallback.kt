package com.ofek.hunter.interfaces

import com.ofek.hunter.models.CVFile

/**
 * Callback interface for CV file item interactions.
 */
interface CVCallback {
    fun itemClicked(cv: CVFile, position: Int)
    fun deleteClicked(cv: CVFile, position: Int)
    fun shareClicked(cv: CVFile, position: Int)
}
