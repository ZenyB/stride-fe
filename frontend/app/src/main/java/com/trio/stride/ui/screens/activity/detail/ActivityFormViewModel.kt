package com.trio.stride.ui.screens.activity.detail

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.remote.dto.CreateActivityRequestDTO
import com.trio.stride.data.remote.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.Activity
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.usecase.file.UploadFileUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ActivityFormViewModel @Inject constructor(
    private val sportManager: SportManager,
    private val uploadFileUseCase: UploadFileUseCase,
) : BaseViewModel<ActivityFormViewModel.ViewState>() {
    override fun createInitialState(): ViewState = ViewState()

    private val _categories = sportManager.categories
    val categories: StateFlow<List<Category>> = _categories

    private val _sportsByCategory = sportManager.sportsByCategory
    val sportsByCategory: StateFlow<Map<Category, List<Sport>>> = _sportsByCategory

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val fileName = queryName(contentResolver, uri)
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", fileName, context.cacheDir)
        tempFile.outputStream().use { fileOut ->
            inputStream?.copyTo(fileOut)
        }
        return tempFile
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String {
        var name = "temp_file"
        val returnCursor = resolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    fun uploadImages(images: List<Uri>, context: Context, onFinish: () -> Unit) {
        setState { currentState.copy(isUploadImage = true) }
        viewModelScope.launch {
            val uploadedUrls = coroutineScope {
                images.map { uri ->
                    async {
                        var result = ""
                        uploadFileUseCase.invoke(uri, context).first { response ->
                            when (response) {
                                is Resource.Success -> {
                                    result = response.data
                                    true
                                }

                                is Resource.Error -> {
                                    // handle error if needed
                                    true
                                }

                                is Resource.Loading -> false
                            }
                        }
                        result
                    }
                }.awaitAll()
            }

            setState { currentState.copy(isUploadImage = false) }
            val newActivityImages = currentState.activity.images.toMutableList()
            uploadedUrls.forEach { newActivityImages.add(it) }
            setState {
                currentState.copy(
                    createActivityDto = currentState.createActivityDto.copy(images = newActivityImages),
                    updateActivityDto = currentState.updateActivityDto.copy(images = newActivityImages),
                )
            }
            onFinish()
        }
    }

    fun updateName(value: String) {
        setState {
            currentState.copy(
                updateActivityDto = updateActivityDto.copy(name = value),
                createActivityDto = createActivityDto.copy(name = value)
            )
        }
    }

    fun updateDescription(value: String) {
        setState {
            currentState.copy(
                updateActivityDto = updateActivityDto.copy(description = value),
                createActivityDto = createActivityDto.copy(description = value)
            )
        }
    }

    fun updateSport(value: Sport) {
        setState {
            currentState.copy(
                sport = value,
                updateActivityDto = updateActivityDto.copy(sportId = value.id),
                createActivityDto = createActivityDto.copy(sportId = value.id)
            )
        }
    }

    fun updateFeelingRate(value: Int) {
        setState {
            currentState.copy(
                updateActivityDto = updateActivityDto.copy(rpe = value),
                createActivityDto = createActivityDto.copy(rpe = value),
                isRpeChanged = true,
            )
        }
    }

    fun updateActivityImage(images: List<String>) {
        setState {
            currentState.copy(
                activity = currentState.activity.copy(
                    images = images
                )
            )
        }
    }

    fun initial(mode: ActivityFormMode) {
        when (mode) {
            is ActivityFormMode.Create ->
                setState {
                    currentState.copy(
                        sport = mode.sportFromRecord!!,
                        createActivityDto = CreateActivityRequestDTO(
                            sportId = mode.sportFromRecord.id
                        )
                    )
                }

            is ActivityFormMode.Update ->
                setState {
                    currentState.copy(
                        activity = mode.activity,
                        updateActivityDto = UpdateActivityRequestDto(
                            sportId = mode.activity.sport.id,
                            rpe = mode.activity.rpe,
                            name = mode.activity.name,
                            description = mode.activity.description,
                            images = mode.activity.images
                        )
                    )
                }
        }
    }

    fun setIsRpeChanged(value: Boolean) {
        setState { currentState.copy(isRpeChanged = value) }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val isRpeChanged: Boolean = false,
        val isUploadImage: Boolean = false,
        val activity: Activity = Activity(),
        val sport: Sport = Sport(),
        val updateActivityDto: UpdateActivityRequestDto = UpdateActivityRequestDto(),
        val createActivityDto: CreateActivityRequestDTO = CreateActivityRequestDTO(),
    ) : IViewState
}