package dev.lvhung14.delta_v.ui.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lvhung14.delta_v.R

@Composable
fun LaunchCard(
    image: Int,
    name: String,
    desc: String,
    countDown: String,
    day: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(image), contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxHeight()
        )
        Column(modifier = Modifier
            .weight(weight = 2f)
            .fillMaxHeight()
            .padding(12.dp)) {
            LaunchName(name)
            LaunchDescription(desc)
            LaunchCountDownTimer(countDown)
            LaunchDay(day)
        }
    }
}

@Composable
fun LaunchName(launchName: String, modifier: Modifier = Modifier) {
    Text(text = launchName, style = MaterialTheme.typography.bodyLarge, modifier = modifier)
}

@Composable
fun LaunchDescription(launchDescription: String, modifier: Modifier = Modifier) {
    Text(text = launchDescription, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun LaunchCountDownTimer(time: String, modifier: Modifier = Modifier) {
    Text(text = time)
}

@Composable
fun LaunchDay(day: String, modifier: Modifier = Modifier) {
    Text(text = day)
}

@Composable
fun BottomSheetContent(modifier: Modifier = Modifier) {
    Row {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null
        )
    }
}

@Composable
@Preview
fun LaunchCardPreview() {
    LaunchCard(R.drawable.test_image, "Haha", "Hihi", "1111", "1111")
}