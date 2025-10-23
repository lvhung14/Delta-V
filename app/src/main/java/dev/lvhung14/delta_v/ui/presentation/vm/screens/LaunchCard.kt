package dev.lvhung14.delta_v.ui.presentation.vm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.lvhung14.delta_v.R
import kotlinx.coroutines.launch

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
            painter = painterResource(image), contentDescription = null
        )
        Column {
            Text(name)
            Text(desc)
            Text(countDown)
            Text(day)
        }
    }
}

@Composable
fun LaunchName(launchName: String, modifier: Modifier = Modifier) {
    Text(text = launchName, style = MaterialTheme.typography.bodyLarge, modifier = modifier)
}

@Composable
fun LaunchDescription(launchDescription: String) {
    Text(text = launchDescription, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun LaunchCountDownTimer(time: String) {
    Text(text = time)
}

@Composable
fun BottomSheetContent() {
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