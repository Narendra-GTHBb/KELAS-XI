package com.apk.listdemoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apk.listdemoapp.ui.theme.ListDemoAppTheme

// Data class untuk Marvel Character
data class MarvelChar(
    val name: String,
    val actorName: String,
    val imageRes: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListDemoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MarvelCharacterList()
                }
            }
        }
    }
}

@Composable
fun MarvelCharacterList() {
    val context = LocalContext.current
    
    // List data Marvel Characters
    val marvelCharacters = listOf(
        MarvelChar("Thor", "Chris Hemsworth", R.drawable.thor),
        MarvelChar("Iron Man", "Robert Downey Jr", R.drawable.ironman),
        MarvelChar("Captain America", "Chris Evans", R.drawable.captain_america),
        MarvelChar("Black Widow", "Scarlett Johansson", R.drawable.blackwidow),
        MarvelChar("Hulk", "Mark Ruffalo", R.drawable.hulk),
        MarvelChar("Spider-Man", "Tom Holland", R.drawable.spiderman),
        MarvelChar("Doctor Strange", "Benedict Cumberbatch", R.drawable.doctorstrange),
        MarvelChar("Black Panther", "Chadwick Boseman", R.drawable.blackpanther)
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(marvelCharacters) { character ->
            MarvelCharacterRow(
                character = character,
                onClick = {
                    Toast.makeText(
                        context,
                        "Clicked ${character.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
fun MarvelCharacterRow(
    character: MarvelChar,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Image
            Image(
                painter = painterResource(id = character.imageRes),
                contentDescription = character.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Character Info
            Column {
                Text(
                    text = character.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = character.actorName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarvelCharacterListPreview() {
    ListDemoAppTheme {
        MarvelCharacterList()
    }
}