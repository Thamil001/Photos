package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.util.Image
import com.example.myapplication.util.ImageResolver
import com.example.myapplication.util.PermissionManager
import com.example.myapplication.util.getImageUriFromPath
import com.example.myapplication.util.toast
import kotlinx.coroutines.delay

val SkyBlue = Color(0xFF448AFF)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!PermissionManager.checkStoragePermission(this)) {
            PermissionManager.requestStoragePermission(this)
        } else setUi()
    }

    private fun setUi() {
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppMain()
                }
            }

        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        try {
            setUi()
        } catch (e: Exception) {
            toast("Permission denied!")
            finishAffinity()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

// all apps integrated
@Composable
fun AppMain() {
    TopBarIntegrated()
}

//Top bar integrated
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarIntegrated() {
    val customH4TextStyle = TextStyle(
        fontSize = 25.sp,
        fontWeight = FontWeight.Thin,
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photos", color = SkyBlue, style = customH4TextStyle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Transparent
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopStart
            ) {
                PreviewImageGrid()
            }
        }
    )
}

//loading page
@Composable
fun PreviewImageGrid() {
    var isLoading by remember { mutableStateOf(true) }
    val images = remember { mutableStateListOf<Image>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        images.clear()
        images.addAll(ImageResolver.query(context))
        isLoading = false
    }

    if (isLoading)
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TypewriterAnimation()
        }
    else
        ImageGrid(imageList = images)
}

//Animation for LauncherEffect
@Composable
fun TypewriterAnimation() {
    val customH4TextStyle = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.Normal,
    )

    var visibleText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val text = "Photos"
        for (i in text.indices) {
            delay(10) // Adjust delay as needed for the speed of typing
            visibleText = text.substring(0, i + 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = visibleText,
            color = SkyBlue,
            style = customH4TextStyle
        )
    }
}


// split the column
@Composable
fun ImageGrid(imageList: List<Image>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(120.dp)) {
        itemsIndexed(imageList) { index, image ->
            GridItem(image = image, index = index, imageList = imageList)

        }
    }

}

// align the all items in gridview
@Composable
fun GridItem(image: Image, index: Int, imageList: List<Image>) {
    var isFullScreen by remember { mutableStateOf(false) }
    var isLongPressed by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(index) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(0.6.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isLongPressed = true
                    },
                    onPress = {
                        tryAwaitRelease()
                        isLongPressed = false
                    },
                    onTap = {
                        isFullScreen = true
                        currentIndex = index
                    }
                )

            }

    ) {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(image.uri) {
            bitmap = ImageResolver.getThumbnail(context, image.uri)
            isLoading = false
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        } else {
            bitmap?.let { img ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(img)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Image $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } ?: AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(image.data)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image $index",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isFullScreen) {
            FullScreenImagePager(
                imageList = imageList,
                initialPage = currentIndex
            ) { isFullScreen = false }
        }

        if (isLongPressed) {
            LongPressDialog(
                image = image,
                onDismiss = { isLongPressed = false }
            )
        }
    }
}


// fullscreen image view and swipe the image go to next image
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenImagePager(imageList: List<Image>, initialPage: Int, onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = initialPage) { imageList.size }
    var showBars by remember { mutableStateOf(true) }
    val context = LocalContext.current


    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
    }

    val shareContent = { imageUri: Uri ->
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this content!")
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/*"
        }
        val shareIntent = Intent.createChooser(intent, null)
        shareLauncher.launch(shareIntent)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount > 0) {
                                onDismiss()
                            }
                        }
                    }
            ) {
                HorizontalPager(
                    state = pagerState
                ) { page ->
                    var isZoomable by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                isZoomable = !isZoomable
                                showBars = !showBars
                            }
                    ) {
                        if (isZoomable) {
                            ZoomableAsyncImage(
                                imageUrl = imageList[page].data,
                                contentDescription = null
                            )
                        } else {
                            AsyncImage(
                                model = imageList[page].data,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                if (showBars) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val title = imageList[pagerState.currentPage].data.let { path ->
                            path.substring(path.lastIndexOf("/") + 1)
                        }

                        CustomTopBar(title)
                        Spacer(modifier = Modifier.weight(1f)) // This ensures the bottom bar is at the bottom

                        CustomBottomBar(
                            onShareClick = {
                                val currentPage = pagerState.currentPage
                                val imageUri =
                                    getImageUriFromPath(context, imageList[currentPage].data)
                                if (imageUri != null) {
                                    shareContent(imageUri)
                                }
                            }
                        )
                    }
                }
            }
        }

    }


}

@Composable
fun CustomBottomBar(
    onShareClick: () -> Unit

) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Black)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Icon",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CustomTopBar(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(16.dp)
            .height(30.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}


// Dialog displayed on long press
@Composable
fun LongPressDialog(image: Image, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Transparent)
        ) {


            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image.data)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}

// Zoom in Zoom out image view
@Composable
fun ZoomableAsyncImage(imageUrl: String, contentDescription: String?) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val minScale = 1f
    val maxScale = 4f
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                    val scaleChange = newScale / scale
                    scale = newScale
                    offsetX = (offsetX * scaleChange + pan.x * newScale).coerceIn(
                        -(context.resources.displayMetrics.widthPixels * (newScale - 1)) / 2,
                        (context.resources.displayMetrics.widthPixels * (newScale - 1)) / 2
                    )
                    offsetY = (offsetY * scaleChange + pan.y * newScale).coerceIn(
                        -(context.resources.displayMetrics.heightPixels * (newScale - 1)) / 2,
                        (context.resources.displayMetrics.heightPixels * (newScale - 1)) / 2
                    )
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}





