package com.example.weatherstation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ESP32ConfigDialog(
    currentIP: String,
    onIPChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConnect: () -> Unit
) {
    var ipAddress by remember { mutableStateOf(currentIP) }
    var isValidIP by remember { mutableStateOf(true) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ESP32 Configuration",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { 
                        ipAddress = it
                        isValidIP = isValidIPAddress(it)
                    },
                    label = { Text("ESP32 IP Address") },
                    placeholder = { Text("192.168.1.100") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "IP Address"
                        )
                    },
                    isError = !isValidIP,
                    supportingText = {
                        if (!isValidIP) {
                            Text(
                                text = "Please enter a valid IP address",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Enter your ESP32's local IP address")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isValidIP) {
                                onIPChanged(ipAddress)
                                onConnect()
                                onDismiss()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isValidIP) {
                                onIPChanged(ipAddress)
                                onConnect()
                                onDismiss()
                            }
                        },
                        enabled = isValidIP
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Connect")
                    }
                }
            }
        }
    }
}

private fun isValidIPAddress(ip: String): Boolean {
    if (ip.isBlank()) return false
    
    val parts = ip.split(".")
    if (parts.size != 4) return false
    
    return try {
        parts.all { part ->
            val num = part.toInt()
            num in 0..255
        }
    } catch (e: NumberFormatException) {
        false
    }
}
