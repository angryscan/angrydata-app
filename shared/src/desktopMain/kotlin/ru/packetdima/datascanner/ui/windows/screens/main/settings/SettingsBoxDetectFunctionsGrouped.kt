package ru.packetdima.datascanner.ui.windows.screens.main.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.downdetector.bigdatascanner.common.DetectFunction
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.scan.functions.RKNDomainDetectFun
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.components.DetectFunctionTooltip

data class DetectionGroup(
    val name: String,
    val functions: List<DetectFunction>,
    val additionalFunctions: List<Any> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBoxDetectFunctionsGrouped(
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
) {
    val detectFunctions = remember { scanSettings.detectFunctions }
    var expanded by remember { scanSettings.detectFunctionsExpanded }
    var detectCode by remember { scanSettings.detectCode }
    var detectCert by remember { scanSettings.detectCert }
    var detectDomains by remember { scanSettings.detectBlockedDomains }

    LaunchedEffect(detectFunctions, expanded, detectCert, detectCode, detectDomains) {
        scanSettings.save()
    }

    val personalDataNumbersName = stringResource(Res.string.DetectGroup_PersonalDataNumbers)
    val personalDataTextName = stringResource(Res.string.DetectGroup_PersonalDataText)
    val bankingSecrecyName = stringResource(Res.string.DetectGroup_BankingSecrecy)
    val itAssetsName = stringResource(Res.string.DetectGroup_ITAssets)

    val detectionGroups = remember(
        personalDataNumbersName,
        personalDataTextName,
        bankingSecrecyName,
        itAssetsName
    ) {
        listOf(
            DetectionGroup(
                name = personalDataNumbersName,
                functions = listOf(
                    DetectFunction.Phones,
                    DetectFunction.CarNumber,
                    DetectFunction.SNILS,
                    DetectFunction.Passport,
                    DetectFunction.OMS,
                    DetectFunction.INN
                )
            ),
            DetectionGroup(
                name = personalDataTextName,
                functions = listOf(
                    DetectFunction.Name,
                    DetectFunction.Emails,
                    DetectFunction.Address,
                    DetectFunction.ValuableInfo,
                    DetectFunction.Login,
                    DetectFunction.Password
                )
            ),
            DetectionGroup(
                name = bankingSecrecyName,
                functions = listOf(
                    DetectFunction.CardNumbers,
                    DetectFunction.AccountNumber,
                    DetectFunction.CVV
                )
            ),
            DetectionGroup(
                name = itAssetsName,
                functions = listOf(
                    DetectFunction.IP,
                    DetectFunction.IPv6
                ),
                additionalFunctions = listOf(
                    CodeDetectFun,
                    CertDetectFun,
                    RKNDomainDetectFun
                )
            )
        )
    }

    SettingsBoxSpan(
        text = stringResource(Res.string.ScanSettings_DetectFunctions),
        expanded = expanded,
        onExpandClick = {
            expanded = !expanded
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MinimalSelectAllButton(scanSettings = scanSettings)

            detectionGroups.forEach { group ->
                MinimalDetectionGroupCard(
                    group = group,
                    scanSettings = scanSettings
                )
            }
        }
    }
}

@Composable
private fun MinimalSelectAllButton(
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
) {
    val isAllSelected = scanSettings.detectFunctions.containsAll(DetectFunction.entries)
            && scanSettings.detectCert.value
            && scanSettings.detectCode.value
            && scanSettings.detectBlockedDomains.value

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isAllSelected) {
                    scanSettings.detectFunctions.clear()
                    scanSettings.detectCert.value = false
                    scanSettings.detectCode.value = false
                    scanSettings.detectBlockedDomains.value = false
                } else {
                    scanSettings.detectFunctions.addAll(DetectFunction.entries.filter {
                        !scanSettings.detectFunctions.contains(it)
                    })
                    scanSettings.detectCert.value = true
                    scanSettings.detectCode.value = true
                    scanSettings.detectBlockedDomains.value = true
                }
                scanSettings.save()
            },
        shape = RoundedCornerShape(8.dp),
        color = if (isHovered) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else 
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shadowElevation = if (isHovered) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isHovered) 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                else 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                modifier = Modifier.size(24.dp)
            ) {
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { checked ->
                        if (checked) {
                            scanSettings.detectFunctions.addAll(DetectFunction.entries.filter {
                                !scanSettings.detectFunctions.contains(it)
                            })
                            scanSettings.detectCert.value = true
                            scanSettings.detectCode.value = true
                            scanSettings.detectBlockedDomains.value = true
                        } else {
                            scanSettings.detectFunctions.clear()
                            scanSettings.detectCert.value = false
                            scanSettings.detectCode.value = false
                            scanSettings.detectBlockedDomains.value = false
                        }
                        scanSettings.save()
                    },
                    modifier = Modifier.size(18.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                    )
                )
            }
            
            Text(
                text = stringResource(Res.string.ScanSettings_SelectAll),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isHovered) 
                    MaterialTheme.colorScheme.primary
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun isGroupFullySelected(
    group: DetectionGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
): Boolean {
    val allFunctionsSelected = group.functions.all { function ->
        scanSettings.detectFunctions.contains(function)
    }

    val allAdditionalFunctionsSelected = group.additionalFunctions.all { additionalFunction ->
        when (additionalFunction) {
            is CodeDetectFun -> scanSettings.detectCode.value
            is CertDetectFun -> scanSettings.detectCert.value
            is RKNDomainDetectFun -> scanSettings.detectBlockedDomains.value
            else -> false
        }
    }

    return allFunctionsSelected && allAdditionalFunctionsSelected
}

@Composable
private fun isGroupPartiallySelected(
    group: DetectionGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
): Boolean {
    val anyFunctionSelected = group.functions.any { function ->
        scanSettings.detectFunctions.contains(function)
    }

    val anyAdditionalFunctionSelected = group.additionalFunctions.any { additionalFunction ->
        when (additionalFunction) {
            is CodeDetectFun -> scanSettings.detectCode.value
            is CertDetectFun -> scanSettings.detectCert.value
            is RKNDomainDetectFun -> scanSettings.detectBlockedDomains.value
            else -> false
        }
    }

    val isFullySelected = isGroupFullySelected(group, scanSettings)
    return (anyFunctionSelected || anyAdditionalFunctionSelected) && !isFullySelected
}

@Composable
private fun MinimalDetectionGroupCard(
    group: DetectionGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
) {
    var groupExpanded by remember { mutableStateOf(false) }
    
    val groupIcon = when (group.name) {
        stringResource(Res.string.DetectGroup_PersonalDataNumbers) -> Icons.Default.Person
        stringResource(Res.string.DetectGroup_PersonalDataText) -> Icons.Default.Description
        stringResource(Res.string.DetectGroup_BankingSecrecy) -> Icons.Default.Security
        stringResource(Res.string.DetectGroup_ITAssets) -> Icons.Default.Storage
        else -> Icons.Default.Category
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val isFullySelected = isGroupFullySelected(group, scanSettings)
    val isPartiallySelected = isGroupPartiallySelected(group, scanSettings)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource = interactionSource),
        shape = RoundedCornerShape(10.dp),
        color = if (isHovered) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
        else 
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shadowElevation = if (isHovered) 3.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { groupExpanded = !groupExpanded },
                color = if (isHovered) 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                else 
                    Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isHovered) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        else 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Checkbox(
                            checked = isFullySelected,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    group.functions.forEach { function ->
                                        if (!scanSettings.detectFunctions.contains(function)) {
                                            scanSettings.detectFunctions.add(function)
                                        }
                                    }
                                    group.additionalFunctions.forEach { additionalFunction ->
                                        when (additionalFunction) {
                                            is CodeDetectFun -> scanSettings.detectCode.value = true
                                            is CertDetectFun -> scanSettings.detectCert.value = true
                                            is RKNDomainDetectFun -> scanSettings.detectBlockedDomains.value = true
                                        }
                                    }
                                } else {
                                    group.functions.forEach { function ->
                                        scanSettings.detectFunctions.remove(function)
                                    }
                                    
                                    group.additionalFunctions.forEach { additionalFunction ->
                                        when (additionalFunction) {
                                            is CodeDetectFun -> scanSettings.detectCode.value = false
                                            is CertDetectFun -> scanSettings.detectCert.value = false
                                            is RKNDomainDetectFun -> scanSettings.detectBlockedDomains.value = false
                                        }
                                    }
                                }
                                scanSettings.save()
                            },
                            modifier = Modifier.size(18.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = if (isPartiallySelected) 
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                else 
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isHovered) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = groupIcon,
                            contentDescription = null,
                            tint = if (isHovered) 
                                MaterialTheme.colorScheme.primary
                            else 
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(4.dp)
                        )
                    }

                    Text(
                        text = group.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isHovered) 
                            MaterialTheme.colorScheme.primary
                        else 
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (groupExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = if (isHovered) 
                            MaterialTheme.colorScheme.primary
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { groupExpanded = !groupExpanded }
                    )
                }
            }

            AnimatedVisibility(
                visible = groupExpanded,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                        group.functions.forEach { detectFunction ->
                            ModernDetectionFunctionItem(
                                detectFunction = detectFunction,
                                scanSettings = scanSettings
                            )
                        }

                        group.additionalFunctions.forEach { additionalFunction ->
                            when (additionalFunction) {
                                is CodeDetectFun -> {
                                    ModernDetectionFunctionItem(
                                        detectFunction = null,
                                        scanSettings = scanSettings,
                                        isCode = true
                                    )
                                }
                                is CertDetectFun -> {
                                    ModernDetectionFunctionItem(
                                        detectFunction = null,
                                        scanSettings = scanSettings,
                                        isCert = true
                                    )
                                }
                                is RKNDomainDetectFun -> {
                                    ModernDetectionFunctionItem(
                                        detectFunction = null,
                                        scanSettings = scanSettings,
                                        isDomain = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
private fun ModernDetectionFunctionItem(
    detectFunction: DetectFunction?,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings,
    isCode: Boolean = false,
    isCert: Boolean = false,
    isDomain: Boolean = false
) {
    val isChecked = when {
        detectFunction != null -> scanSettings.detectFunctions.contains(detectFunction)
        isCode -> scanSettings.detectCode.value
        isCert -> scanSettings.detectCert.value
        isDomain -> scanSettings.detectBlockedDomains.value
        else -> false
    }

    val onCheckedChange = { checked: Boolean ->
        when {
            detectFunction != null -> {
                if (checked && !scanSettings.detectFunctions.contains(detectFunction))
                    scanSettings.detectFunctions.add(detectFunction)
                else if (!checked)
                    scanSettings.detectFunctions.remove(detectFunction)
            }
            isCode -> scanSettings.detectCode.value = checked
            isCert -> scanSettings.detectCert.value = checked
            isDomain -> scanSettings.detectBlockedDomains.value = checked
        }
        scanSettings.save()
    }

    val functionName = when {
        detectFunction != null -> detectFunction.composableName()
        isCode -> stringResource(Res.string.DetectFunction_Code)
        isCert -> stringResource(Res.string.DetectFunction_Cert)
        isDomain -> stringResource(Res.string.DetectFunction_DetectBlockedDomains)
        else -> ""
    }

    val functionForTooltip = when {
        detectFunction != null -> detectFunction
        isCode -> CodeDetectFun
        isCert -> CertDetectFun
        isDomain -> RKNDomainDetectFun
        else -> null
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val itemInteractionSource = remember { MutableInteractionSource() }
    val isItemHovered by itemInteractionSource.collectIsHoveredAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .hoverable(interactionSource = itemInteractionSource),
        shape = RoundedCornerShape(6.dp),
        color = if (isItemHovered) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.06f)
        else 
            Color.Transparent,
        shadowElevation = if (isItemHovered) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(16.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                )
            )

            if (functionForTooltip != null) {
                DetectFunctionTooltip(
                    detectFunction = functionForTooltip
                ) {
                    Text(
                        text = functionName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isItemHovered) 
                            MaterialTheme.colorScheme.primary
                        else 
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = functionName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isItemHovered) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
