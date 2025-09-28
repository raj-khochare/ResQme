package com.mnp.resqme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mnp.resqme.ui.components.CustomButton
import com.mnp.resqme.ui.components.CustomTextField
import com.mnp.resqme.ui.navigation.Screen
import com.mnp.resqme.util.UiState
import com.mnp.resqme.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Join our emergency rescue community",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            text = "Create Account",
            onClick = {
                viewModel.registerUser(email, password, name, confirmPassword)
            },
            isLoading = registerState is UiState.Loading
        )

        if (registerState is UiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (registerState as UiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(
                text = "Already have an account? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = {
                    navController.popBackStack()
                    viewModel.resetState()
                }
            ) {
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}