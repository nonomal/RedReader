package org.quantumbadger.redreader.compose.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.quantumbadger.redreader.common.General
import org.quantumbadger.redreader.common.invokeIf
import org.quantumbadger.redreader.compose.ctx.LocalRedditUser
import org.quantumbadger.redreader.compose.net.NetRequestStatus
import org.quantumbadger.redreader.compose.net.fetchImage
import org.quantumbadger.redreader.compose.theme.LocalComposeTheme
import org.quantumbadger.redreader.image.ImageUrlInfo

@Composable
fun NetImage(
	modifier: Modifier,
	image: ImageUrlInfo?,
	cropToAspect: Float? = null
) {
	val aspectRatio = cropToAspect ?: image?.size?.takeIf { it.height > 0 }?.let {
		it.width.toFloat() / it.height.toFloat()
	}

	val url = image?.url?.let(General::uriFromString)

	if (url == null) {
		// TODO show error
		return
	}

	val data by fetchImage(
		uri = url,
		user = LocalRedditUser.current,
	)

	Box(
		modifier = modifier
            .invokeIf((cropToAspect != null &&
					data !is NetRequestStatus.Success &&
					data !is NetRequestStatus.Failed) || aspectRatio != null) {
                aspectRatio(aspectRatio!!)
            }
            .animateContentSize(),
		contentAlignment = Alignment.Center,
	) {
		val theme = LocalComposeTheme.current

		when (val it = data) {
			NetRequestStatus.Connecting -> {
				CircularProgressIndicator(
					Modifier.padding(24.dp)
				)
			}

			is NetRequestStatus.Downloading -> {
				CircularProgressIndicator(
					modifier = Modifier.padding(24.dp),
					progress = { it.fractionComplete }
				)
			}

			is NetRequestStatus.Failed -> {
				RRErrorView(error = it.error)
			}

			is NetRequestStatus.Success -> {
				Box(Modifier.background(theme.postCard.previewImageBackgroundColor)) {
					Image(
						modifier = Modifier.fillMaxSize(),
						bitmap = it.result,
						contentDescription = null,
						contentScale = if (cropToAspect == null) {
							ContentScale.Fit
						} else {
							ContentScale.Crop
						}
					)
				}
			}
		}
	}
}