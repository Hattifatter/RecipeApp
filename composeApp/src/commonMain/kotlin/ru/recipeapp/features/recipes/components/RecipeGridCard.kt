package ru.recipeapp.features.recipes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ru.recipeapp.designsystem.theme.AppColors
import ru.recipeapp.features.recipes.RecipeCardUi
import recipeapp.composeapp.generated.resources.Res
import recipeapp.composeapp.generated.resources.compose_multiplatform

@Composable
fun RecipeGridCard(
    item: RecipeCardUi,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.FieldBg),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Картинка (пока заглушка из стандартного ресурса)
//            androidx.compose.foundation.Image(
//                painter = painterResource(Res.drawable.compose_multiplatform),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1.15f)
//                    .clip(RoundedCornerShape(14.dp))
//            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Divider)
            )


            Spacer(Modifier.height(8.dp))

            Text(
                text = "@${item.authorHandle}",
                color = AppColors.Placeholder,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = androidx.compose.ui.graphics.Color(0xFF2E3A4D),
                maxLines = 2,
                softWrap = true
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "${item.category} · ${item.durationText}",
                color = AppColors.Placeholder,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
