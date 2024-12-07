package com.codingskillshub.mymistri.calculator.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AssistChip
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.chip.Chip

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
            text = buttonKey
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
            text = buttonKey
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
            text = buttonKey
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
            text = buttonKey
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
        label = { Text(modeName) }
    )
}

@Preview
@Composable
fun PreviewNumberButton() {
    NumberButton(
        onClick = {},
        buttonKey = "1"
    )
}

@Preview
@Composable
fun PreviewEqualButton() {
    EqualButton(
        onClick = {}
    )
}

@Preview
@Composable
fun PreviewOperationButton() {
    OperationButton(
        onClick = {},
        buttonKey = "+"
    )
}

@Preview
@Composable
fun PreviewClearButton() {
    ClearButton(
        onClick = {},
        buttonKey = "C"
    )
}

@Preview
@Composable
fun PreviewBackspaceButton() {
    BackspaceButton(
        onClick = {}
    )
}