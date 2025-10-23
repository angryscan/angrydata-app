package ru.packetdima.datascanner.ui.windows.screens.main.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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

    // Получаем строки для названий групп
    val personalDataNumbersName = stringResource(Res.string.DetectGroup_PersonalDataNumbers)
    val personalDataTextName = stringResource(Res.string.DetectGroup_PersonalDataText)
    val bankingSecrecyName = stringResource(Res.string.DetectGroup_BankingSecrecy)
    val itAssetsName = stringResource(Res.string.DetectGroup_ITAssets)

    // Группировка функций детектирования
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
                    DetectFunction.CardNumbers,
                    DetectFunction.CarNumber,
                    DetectFunction.SNILS,
                    DetectFunction.Passport,
                    DetectFunction.OMS,
                    DetectFunction.INN,
                    DetectFunction.AccountNumber,
                    DetectFunction.CVV
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Современная кнопка "Выбрать все"
            MinimalSelectAllButton(scanSettings = scanSettings)

            // Современные группы функций
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isAllSelected) 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else 
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
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
            }
            .hoverable(interactionSource = interactionSource),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isAllSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
            
            // Отступ для визуального разделения
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = stringResource(Res.string.ScanSettings_SelectAll),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun isGroupFullySelected(
    group: DetectionGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
): Boolean {
    // Проверяем, выбраны ли все обычные функции группы
    val allFunctionsSelected = group.functions.all { function ->
        scanSettings.detectFunctions.contains(function)
    }
    
    // Проверяем, выбраны ли все дополнительные функции группы
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
    // Проверяем, выбрана ли хотя бы одна обычная функция группы
    val anyFunctionSelected = group.functions.any { function ->
        scanSettings.detectFunctions.contains(function)
    }
    
    // Проверяем, выбрана ли хотя бы одна дополнительная функция группы
    val anyAdditionalFunctionSelected = group.additionalFunctions.any { additionalFunction ->
        when (additionalFunction) {
            is CodeDetectFun -> scanSettings.detectCode.value
            is CertDetectFun -> scanSettings.detectCert.value
            is RKNDomainDetectFun -> scanSettings.detectBlockedDomains.value
            else -> false
        }
    }
    
    return anyFunctionSelected || anyAdditionalFunctionSelected
}

@Composable
private fun MinimalDetectionGroupCard(
    group: DetectionGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
) {
    var groupExpanded by remember { mutableStateOf(false) }
    
    val groupIcon = when (group.name) {
        stringResource(Res.string.DetectGroup_PersonalDataNumbers) -> Icons.Default.Numbers
        stringResource(Res.string.DetectGroup_PersonalDataText) -> Icons.Default.TextFields
        stringResource(Res.string.DetectGroup_BankingSecrecy) -> Icons.Default.AccountBalance
        stringResource(Res.string.DetectGroup_ITAssets) -> Icons.Default.Computer
        else -> Icons.Default.Category
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val isFullySelected = isGroupFullySelected(group, scanSettings)
    val isPartiallySelected = isGroupPartiallySelected(group, scanSettings)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource = interactionSource),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Современный заголовок группы
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { groupExpanded = !groupExpanded },
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Современный чекбокс с анимацией
                    Checkbox(
                        checked = isFullySelected,
                        onCheckedChange = { checked ->
                            if (checked) {
                                // Выбираем все функции группы
                                group.functions.forEach { function ->
                                    if (!scanSettings.detectFunctions.contains(function)) {
                                        scanSettings.detectFunctions.add(function)
                                    }
                                }
                                // Выбираем дополнительные функции группы
                                group.additionalFunctions.forEach { additionalFunction ->
                                    when (additionalFunction) {
                                        is CodeDetectFun -> scanSettings.detectCode.value = true
                                        is CertDetectFun -> scanSettings.detectCert.value = true
                                        is RKNDomainDetectFun -> scanSettings.detectBlockedDomains.value = true
                                    }
                                }
                            } else {
                                // Снимаем выбор со всех функций группы
                                group.functions.forEach { function ->
                                    scanSettings.detectFunctions.remove(function)
                                }
                                // Снимаем выбор с дополнительных функций группы
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
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = if (isPartiallySelected) 
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            else 
                                MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    // Отступ для визуального разделения
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // Простая иконка группы
                    Icon(
                        imageVector = groupIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    // Название группы с улучшенной типографикой
                    Text(
                        text = group.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Простая кнопка разворачивания
                    Icon(
                        imageVector = if (groupExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { groupExpanded = !groupExpanded }
                    )
                }
            }

            // Анимированное содержимое группы
            AnimatedVisibility(
                visible = groupExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Обычные функции детектирования
                        group.functions.forEach { detectFunction ->
                            ModernDetectionFunctionItem(
                                detectFunction = detectFunction,
                                scanSettings = scanSettings
                            )
                        }
                        
                        // Дополнительные функции
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .hoverable(interactionSource = interactionSource),
        shape = RoundedCornerShape(8.dp),
        color = if (isHovered) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else 
            Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Современный чекбокс
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(16.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
            
            // Отступ для визуального разделения
            Spacer(modifier = Modifier.width(4.dp))
            
            // Название функции с улучшенной типографикой
            if (functionForTooltip != null) {
                DetectFunctionTooltip(
                    detectFunction = functionForTooltip
                ) {
                    Text(
                        text = functionName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = functionName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MinimalDetectionFunctionItem(
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(16.dp)
        )
        
        if (functionForTooltip != null) {
            DetectFunctionTooltip(
                detectFunction = functionForTooltip
            ) {
                Text(
                    text = functionName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Text(
                text = functionName,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}