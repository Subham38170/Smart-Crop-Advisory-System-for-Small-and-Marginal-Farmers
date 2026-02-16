import tensorflow as tf
import numpy as np
from PIL import Image
from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse
import json
import io
from google import genai
from google.genai import types
from Models import FertilizerInput

from Models import MarketTrend



#python -m uvicorn app:app --reload
# ngrok http 8000q7

#ngrok token : 32oZq8ekM0OLydw3ToCe9bENaFu_2Ds2LASsFb53q94SYEmqj
#Command : ngrok http 8000



# --- Paths ---
model_path = "plant_disease_prediction_model.h5"   # your .h5 file
class_indices_path = "class_indices.json"          # your saved class indices

# --- Load model & classes (load once) ---
print("🔄 Loading model...")
model = tf.keras.models.load_model(model_path)
with open(class_indices_path, "r", encoding='utf-8') as f:
    class_indices = json.load(f)



app = FastAPI(title="🌿 Plant Disease Prediction API")


# --- Helper function ---
def load_and_preprocess_image(image_bytes, target_size=(224, 224)):
    img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    img = img.resize(target_size)
    img_array = np.array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = img_array.astype("float32") / 255.0
    return img_array


def predict_image_class(lang,image_bytes, top_k=3):
    preprocessed_img = load_and_preprocess_image(image_bytes)
    predictions = model.predict(preprocessed_img)[0]

    # Get top-k predictions
    top_indices = predictions.argsort()[-top_k:][::-1]
    results = []
    for idx in top_indices:
        class_data = class_indices[lang][str(idx)]
        confidence = float(predictions[idx]) * 100
        results.append({"Disease": class_data['disease'], "Description": class_data['description'],'Treatment':class_data['treatment'],'Precautions':class_data['precautions'], "confidence": round(confidence, 2)})
    return results


@app.get("/")
def home():
    return {"message": "🌿 Welcome to Plant Disease Prediction API. Use /predict endpoint to upload an image."}



@app.post("/predict")
async def predict(lang: str,file: UploadFile = File(...)):
    try:
        image_bytes = await file.read()
        results = predict_image_class(lang,image_bytes, top_k=3)
        return JSONResponse(content={"predictions": results})
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)


    """
    Recommend fertilizer and return detailed info from fertilizer_data.json
    """

    # Get seasonal weather for the state
    seasonal_weather_data = get_seasonal_weather(data.state)

    # Convert manure_used to boolean
    is_manure_used = data.manure_used.lower() == 'yes'

    # Prepare farmer input
    example_farmer = data.model_dump()
    example_farmer["manure_used"] = is_manure_used

    # Predict NPK values
    npk_values = predict_npk(example_farmer, seasonal_weather_data)

    # Predict fertilizer name (e.g., "Urea", "DAP")
    fertilizer_prediction = predict_fertilizer(npk_values)

    # Pick correct language block, default to English if key not found
    lang_block = fertilizer_data.get(lang, fertilizer_data["eng"])

    # Map fertilizer name → JSON object
    fertilizer_info = next(
        (f for f in lang_block if f["common_name"].lower() == fertilizer_prediction.lower()),
        {
            "common_name": fertilizer_prediction,
            "scientific_name": "Unknown",
            "description": "No information available for this fertilizer.",
            "recommended_crops": []
        }
    )

    return {
        "predicted_npk": {
            "nitrogen": npk_values[0],
            "phosphorous": npk_values[1],
            "potassium": npk_values[2],
        },
        "recommended_fertilizer": fertilizer_info
    }





@app.get("/get_trend/{state}/{district}/{market}/{commodity}")
def get_market_trend_path(state: str, district: str, market: str, commodity: str):
    return get_price_data(state, district, market, commodity)