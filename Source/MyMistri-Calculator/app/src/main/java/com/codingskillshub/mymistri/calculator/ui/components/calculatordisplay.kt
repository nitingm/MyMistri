package com.codingskillshub.mymistri.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Composable
fun CalculatorDisplay(
    inputExpression: String = "",
    outputValue: String = "",
    onInputExpressionChanged: (String) -> Unit
)
{
//    var text by remember { mutableStateOf(TextFieldValue(""))}
    Box(modifier = Modifier.layoutId("card")) {
        val constraints = getCalculatorDisplayConstraints()
        ConstraintLayout(constraints,
            modifier = Modifier.fillMaxSize()) {

            TextField(
                value = inputExpression,
                onValueChange = { newExpression ->
                    onInputExpressionChanged(newExpression)
                } ,

                keyboardOptions = KeyboardOptions(showKeyboardOnFocus = false),
                modifier = Modifier
                .fillMaxWidth()
                .layoutId("inputText")
                .padding(0.dp, 10.dp,5.dp,0.dp)
                .background(Color.Transparent) ,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent, // Set container color to transparent
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black, // Set text color to black (or your desired color)
                    focusedIndicatorColor = Color.Transparent, // Remove focus indicator
                    unfocusedIndicatorColor = Color.Transparent // Remove unfocused indicator
                ),
                textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.End)
            )
            Text(
                text = outputValue,
                modifier = Modifier
                    .size(300.dp, 80.dp)
                    .layoutId("outputText")
                    .padding(0.dp, 10.dp,5.dp,0.dp),
                textAlign = TextAlign.End
            )


        }
    }
}

private fun getCalculatorDisplayConstraints() : ConstraintSet {
    return ConstraintSet {

        val inputText = createRefFor("inputText")
        val outputText = createRefFor("outputText")

        constrain(inputText) {
            end.linkTo(parent.end)
        }
        constrain(outputText) {
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}

@Preview
@Composable
fun CalculatorDisplayPreview()
{
    Box(modifier = Modifier.size(500.dp, 200.dp)) {
        CalculatorDisplay("", "",{})
    }
}