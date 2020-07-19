package net.dejanjokic.arsmovies.ui.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.dejanjokic.arsmovies.data.domain.DomainDetails

sealed class MovieDetailsState {
    @Parcelize data class Success(val item: DomainDetails) : MovieDetailsState(), Parcelable
    @Parcelize data class Error(val message: String) : MovieDetailsState(), Parcelable
    @Parcelize object Loading : MovieDetailsState(), Parcelable
}