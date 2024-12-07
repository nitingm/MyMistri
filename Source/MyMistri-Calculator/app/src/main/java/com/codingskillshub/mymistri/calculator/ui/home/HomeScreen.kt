package com.codingskillshub.mymistri.calculator.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel

import com.codingskillshub.mymistri.calculator.ui.components.*
import com.codingskillshub.mymistri.calculator.viewmodels.CalculatorViewModel
import com.codingskillshub.mymistri.calculator.viewmodels.Mode
import kotlinx.coroutines.launch

//import com.codingskillshub.mymistri.calculator.databinding.FragmentHomeBinding

@Composable
fun HomeScreen(

) {
    var calculatorViewModel = viewModel<CalculatorViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Collect Snackbar events from the ViewModel
    LaunchedEffect(key1 = calculatorViewModel.snackbarEvent) {
        calculatorViewModel.snackbarEvent.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message = message)
            }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    )  {
        Box(modifier = Modifier.weight(1f)) {
            CalculatorDisplay(
                inputExpression = calculatorViewModel.inputExpression,
                outputValue = calculatorViewModel.outputValue,
                {value -> calculatorViewModel.updateInputExpression(value) }
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModeChip(onClick = {calculatorViewModel.toggleMode()}, modeName = calculatorViewModel.modeName)
            Box(modifier = Modifier.weight(1f))
            BackspaceButton(onClick = {calculatorViewModel.removeLastChar()})
        }

        Divider(modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ClearButton({calculatorViewModel.updateInputExpression("")},"CE")
            NumberButton({calculatorViewModel.addOperator('(')},"()")
            NumberButton({calculatorViewModel.addOperator('%')},"%")
            OperationButton({calculatorViewModel.addOperator('/')},"/")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton({calculatorViewModel.addOperand('7')},"7")
            NumberButton({calculatorViewModel.addOperand('8')},"8")
            NumberButton({calculatorViewModel.addOperand('9')},"9")
            OperationButton({calculatorViewModel.addOperator('*')},"x")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton({calculatorViewModel.addOperand('4')},"4")
            NumberButton(onClick = {calculatorViewModel.addOperand('5')}, buttonKey = "5")
            NumberButton({calculatorViewModel.addOperand('6')},"6")
            OperationButton({calculatorViewModel.addOperator('-')},"-")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton({calculatorViewModel.addOperand('1')},"1")
            NumberButton({calculatorViewModel.addOperand('2')},"2")
            NumberButton({calculatorViewModel.addOperand('3')},"3")
            OperationButton({calculatorViewModel.addOperator('+')},"+")
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton({calculatorViewModel.addOperand('0')},"0")
            NumberButton({calculatorViewModel.addOperand('\'')},"\'")
            EqualButton({calculatorViewModel.calculateResult()})
        }
    }
    SnackbarHost(hostState = snackbarHostState)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview(

){
    HomeScreen()
}