import os
import json
import numpy as np
import tensorflow as tf
from PIL import Image
from flask import Flask, request, render_template
import time
import response

# Initialize Flask app
app = Flask(__name__, template_folder=".")

# Paths
working_dir = os.path.dirname(os.path.abspath(__file__))

model_path = os.path.join(working_dir, "model.h5")
class_indices_path = os.path.join(working_dir, "class_indices.json")

# Load model & class indices
print("🔄 Loading model...")
model = tf.keras.models.load_model(model_path)

with open(class_indices_path, "r") as f:
    class_indices = json.load(f)

print("✅ Model Loaded Successfully!")

# -------- Helper Functions -------- #

def load_and_preprocess_image(image_path, target_size=(224, 224)):
    img = Image.open(image_path).convert("RGB")
    img = img.resize(target_size)
    img_array = np.array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = img_array.astype("float32") / 255.0
    return img_array


def predict_image_class(model, image_path, class_indices, top_k=3):
    preprocessed_img = load_and_preprocess_image(image_path)
    predictions = model.predict(preprocessed_img)[0]

    top_indices = predictions.argsort()[-top_k:][::-1]

    results = []
    for idx in top_indices:
        # Get class name safely
        class_name = class_indices.get(str(idx), "Unknown Disease")

        confidence = float(predictions[idx]) * 100

        # Combine name + confidence
        results.append({
            "class": f"{class_name} ({round(confidence,2)}%)",
            "confidence": round(confidence, 2)
        })

    return results



# Fun facts dictionary
fun_facts = {
    "Apple Scab": "🍏 Apple scab thrives in humid environments.",
    "Powdery Mildew": "🌸 Powdery mildew can survive without free water on leaves.",
    "Tomato Late Blight": "🍅 Late blight caused the Irish Potato Famine.",
    "Healthy": "🌱 Your plant looks healthy! Maintain good sunlight and watering."
}

# -------- Routes -------- #

@app.route("/", methods=["GET"])
def home():
    return render_template("index.html")


@app.route("/predict", methods=["POST"])
def predict():
    if "file" not in request.files:
        return render_template("result.html", error="No file uploaded")

    file = request.files["file"]

    if file.filename == "":
        return render_template("result.html", error="No file selected")

    try:
        # Create static folder if not exists
        static_dir = os.path.join(working_dir, "static")
        os.makedirs(static_dir, exist_ok=True)

        # Save uploaded image
        filename = f"{int(time.time())}_{file.filename}"
        image_path = os.path.join(static_dir, filename)

        file.save(image_path)

        # Predict
        predictions = predict_image_class(model, image_path, class_indices, top_k=3)
        top_prediction = predictions[0]["class"]

        fact = fun_facts.get(
            top_prediction,
            "🌿 Plants are amazing living organisms!"
        )

        image_relative_path = os.path.join("static", filename)
        description = response.get_content(top_prediction)

        return render_template(
            "result.html",
            predictions=predictions,
            image_path=image_relative_path,
            fun_fact=fact,
            description=description
        )

    except Exception as e:
        return render_template("result.html", error=str(e))


# -------- Run App -------- #

if __name__ == "__main__":
    app.run(port=5000, debug=True)
