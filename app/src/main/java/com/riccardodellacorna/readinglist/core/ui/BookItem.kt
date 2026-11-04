package com.riccardodellacorna.readinglist.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.riccardodellacorna.readinglist.core.model.Book

@Composable
fun BookItem(
    modifier: Modifier = Modifier,
    book: Book,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = book.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(
                    width = 56.dp,
                    height = 84.dp
                )
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (book.authors.isNotEmpty()) {
                Text(
                    text = book.authors.joinToString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            book.firstPublishYear?.let {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        trailing()
    }
}