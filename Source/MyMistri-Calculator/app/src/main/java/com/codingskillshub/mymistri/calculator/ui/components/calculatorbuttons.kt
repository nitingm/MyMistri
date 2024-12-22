package com.codingskillshub.mymistri.calculator.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AssistChip
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.chip.Chip
import com.codingskillshub.mymistri.calculator.ui.theme.AppTheme
//import java.lang.reflect.Modifier

@Composable
fun NumberButton(
    onClick: () -> Unit,
    buttonKey: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp)
    ) {
        Text(
            text = buttonKey,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun OperationButton(
    onClick: () -> Unit,
    buttonKey: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp)
    ) {
        Text(
            text = buttonKey,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ClearButton(
    onClick: () -> Unit,
    buttonKey: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp)
    ) {
        Text(
            text = buttonKey,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun EqualButton(
    onClick: () -> Unit,
    buttonKey: String = "="
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(170.dp, 80.dp)
    ) {
        Text(
            text = buttonKey,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun BackspaceButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Filled.Backspace,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            contentDescription = "Backspace"
        )
    }
}

@Composable
fun ModeChip(
    onClick: () -> Unit,
    modeName: String
) {
    AssistChip(
        onClick = onClick,
        label = { Text(modeName,
            style = MaterialTheme.typography.titleLarge)
        }
    )
}

@Preview
@Composable
fun PreviewNumberButton() {
    AppTheme {
        NumberButton(
            onClick = {},
            buttonKey = "1"
        )
    }
}

@Preview
@Composable
fun PreviewEqualButton() {
    AppTheme {
        EqualButton(
            onClick = {}
        )
    }
}

@Preview
@Composable
fun PreviewOperationButton() {
    AppTheme {
        OperationButton(
            onClick = {},
            buttonKey = "x"
        )
    }
}

@Preview
@Composable
fun PreviewClearButton() {
    AppTheme {
        ClearButton(
            onClick = {},
            buttonKey = "C"
        )
    }
}

@Preview
@Composable
fun PreviewBackspaceButton() {
    AppTheme {
        BackspaceButton(
            onClick = {}
        )
    }
}

@Preview
@Composable
fun PreviewModeChip() {
    AppTheme {
        ModeChip(
            onClick = {},
            modeName = "Length"
        )
    }
}