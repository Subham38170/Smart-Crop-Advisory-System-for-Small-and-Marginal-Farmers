package com.example.krishimitra.presentation.fertilizer_recommendation


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.R
@Composable
fun FertilizerResultScreen() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Text(
            text = "सिफ़ारिश की गई उर्वरक",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.slight_dark_green)
        )

        // Fertilizer Card
        androidx.compose.material3.Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.light_green)
            )
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                // Fertilizer Name
                Text(
                    text = "उर्वरक का नाम",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "यूरिया",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                // Purpose
                Text(
                    text = "उद्देश्य",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "यह उर्वरक आपके खेत में नाइट्रोजन की कमी को पूरा करता है। नाइट्रोजन से पौधे मजबूत और हरे होते हैं।",
                    color = Color.Black
                )

                // Quantity
                Text(
                    text = "मात्रा",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "50 kg/हेक्टेयर (लगभग 20 kg प्रति एकड़)। अधिक मात्रा से पौधे नुकसान में आ सकते हैं।",
                    color = Color.Black
                )

                // Recommended Crops
                Text(
                    text = "अनुकूल फसल",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "गेहूँ, धान, मक्का। यह उर्वरक इन फसलों में सबसे अच्छा परिणाम देता है।",
                    color = Color.Black
                )

                // Usage Instructions
                Text(
                    text = "कैसे डालें",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "हल्की सिंचाई के बाद मिट्टी में समान रूप से मिलाएँ।",
                    color = Color.Black
                )

                // Special Tips
                Text(
                    text = "विशेष सुझाव",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "अगर पिछली फसल भी नाइट्रोजन ले चुकी थी तो मात्रा कम करें। मिट्टी का प्रकार देखकर सही मात्रा तय करें।",
                    color = Color.Black
                )

                // Caution
                Text(
                    text = "सावधानी",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.slight_dark_green)
                )
                Text(
                    text = "सीधे पौधों पर न डालें, पानी के साथ मिलाकर डालना सुरक्षित है।",
                    color = Color.Black
                )
            }
        }
    }
}
