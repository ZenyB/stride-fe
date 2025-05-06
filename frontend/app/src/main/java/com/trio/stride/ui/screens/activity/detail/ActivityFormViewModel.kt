package com.trio.stride.ui.screens.activity.detail

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.dto.CreateActivityRequestDTO
import com.trio.stride.data.dto.UpdateActivityRequestDto
import com.trio.stride.domain.model.Activity
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

    val sports: StateFlow<List<Sport>> = sportManager.sports

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

    private fun uploadImages(images: List<Uri>, context: Context) {
        viewModelScope.launch {
            val uploadedUrls = coroutineScope {
                images.map { uri ->
                    async {
                        val file = uriToFile(uri, context)
                        var result = ""
                        uploadFileUseCase.invoke(file).first { response ->
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

            val newActivityImages = currentState.activity.images.toMutableList()
            uploadedUrls.forEach { newActivityImages.add(it) }
            setState {
                currentState.copy(
                    activity = currentState.activity.copy(images = newActivityImages)
                )
            }
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
                createActivityDto = createActivityDto.copy(rpe = value)
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

    fun initial(isCreate: Boolean, activity: Activity?, sportFromRecord: Sport?) {
        if (isCreate)
            setState {
                currentState.copy(
                    sport = sportFromRecord!!,
                    createActivityDto = CreateActivityRequestDTO(
                        sportId = sportFromRecord.id
                    )
                )
            }
        else
            setState {
                currentState.copy(
                    activity = activity!!,
                    updateActivityDto = UpdateActivityRequestDto(
                        sportId = activity.sport.id,
                        rpe = activity.rpe,
                        name = activity.name,
                        description = activity.description,
                        images = activity.images
                    )
                )
            }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val activity: Activity = Activity(),
        val sport: Sport = Sport(),
        val updateActivityDto: UpdateActivityRequestDto = UpdateActivityRequestDto(),
        val createActivityDto: CreateActivityRequestDTO = CreateActivityRequestDTO(),
    ) : IViewState

}