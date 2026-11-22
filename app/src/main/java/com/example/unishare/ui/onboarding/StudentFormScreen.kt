package com.example.unishare.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.unishare.data.model.AcademicYear
import com.example.unishare.viewmodel.StudentFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(
    viewModel: StudentFormViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var academicYearExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.saveSuccessEvent.collect {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Registration") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            uiState.university?.let { uni ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected University",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uni.name} (${uni.abbreviation})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(
                text = "Enter your academic details to personalize your offline dashboard.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = { Text("Full Name *") },
                placeholder = { Text("e.g. Alex Johnson") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                },
                isError = uiState.fullNameError != null,
                supportingText = uiState.fullNameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.usnOrCollegeId,
                onValueChange = viewModel::onUsnChange,
                label = { Text("USN / College ID *") },
                placeholder = { Text("e.g. 1MS21CS042") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Badge, contentDescription = null)
                },
                isError = uiState.usnError != null,
                supportingText = uiState.usnError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email Address *") },
                placeholder = { Text("e.g. alex@university.edu") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.emailError != null,
                supportingText = uiState.emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.mobileNumber,
                onValueChange = viewModel::onMobileChange,
                label = { Text("Mobile Number (Optional)") },
                placeholder = { Text("e.g. 9876543210") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = uiState.mobileError != null,
                supportingText = uiState.mobileError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = academicYearExpanded,
                onExpandedChange = { academicYearExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.academicYear,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Academic Year *") },
                    placeholder = { Text("Select your academic year") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.School, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = academicYearExpanded)
                    },
                    isError = uiState.academicYearError != null,
                    supportingText = uiState.academicYearError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = academicYearExpanded,
                    onDismissRequest = { academicYearExpanded = false }
                ) {
                    AcademicYear.entries.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.displayName) },
                            onClick = {
                                viewModel.onAcademicYearChange(year.displayName)
                                academicYearExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Save & Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
