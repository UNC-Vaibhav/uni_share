package com.example.unishare.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.unishare.data.model.AcademicYear
import com.example.unishare.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val profileAndUni by viewModel.profileAndUniState.collectAsState()
    val editState by viewModel.editState.collectAsState()
    val profile = profileAndUni.first
    val university = profileAndUni.second

    var academicYearExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Profile") },
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
        if (profile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val initials = profile.fullName.split(" ")
                .filter { it.isNotBlank() }
                .map { it.first().uppercaseChar() }
                .take(2)
                .joinToString("")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.ifBlank { "ST" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = profile.fullName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = university?.name ?: "University",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.startEditing(profile) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile")
                }

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProfileInfoRow(
                            icon = Icons.Default.Badge,
                            label = "USN / College ID",
                            value = profile.usnOrCollegeId
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email Address",
                            value = profile.email
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Mobile Number",
                            value = profile.mobileNumber ?: "Not provided"
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Academic Year",
                            value = profile.academicYear
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.School,
                            label = "Institution",
                            value = "${university?.name ?: ""} (${university?.abbreviation ?: ""})"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (editState.isEditing) {
        val currentProfile = profile ?: return
        val currentUniId = currentProfile.universityId

        AlertDialog(
            onDismissRequest = viewModel::cancelEditing,
            title = {
                Text("Edit Profile", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editState.editFullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = { Text("Full Name *") },
                        isError = editState.fullNameError != null,
                        supportingText = editState.fullNameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editState.editUsn,
                        onValueChange = viewModel::onUsnChange,
                        label = { Text("USN / College ID *") },
                        isError = editState.usnError != null,
                        supportingText = editState.usnError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editState.editEmail,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Email *") },
                        isError = editState.emailError != null,
                        supportingText = editState.emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editState.editMobile,
                        onValueChange = viewModel::onMobileChange,
                        label = { Text("Mobile Number (Optional)") },
                        isError = editState.mobileError != null,
                        supportingText = editState.mobileError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = academicYearExpanded,
                        onExpandedChange = { academicYearExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = editState.editAcademicYear,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Academic Year *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = academicYearExpanded)
                            },
                            isError = editState.academicYearError != null,
                            supportingText = editState.academicYearError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                            shape = RoundedCornerShape(10.dp),
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
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveProfile(currentUniId) },
                    enabled = !editState.isSaving
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = viewModel::cancelEditing) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
