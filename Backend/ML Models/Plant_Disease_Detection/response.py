# response.py

descriptions = {
    "Apple Scab": "Apple scab is a fungal disease that causes dark, scabby lesions on leaves and fruits. Use fungicides and remove infected leaves.",
    "Powdery Mildew": "Powdery mildew appears as white powdery spots on leaves. Improve air circulation and apply sulfur-based fungicide.",
    "Tomato Late Blight": "Late blight causes brown-black lesions and rapid wilting. Remove infected plants and apply copper fungicide.",
    "Healthy": "Your plant appears healthy. Maintain proper watering, sunlight, and nutrition."
}

def get_content(class_name):
    return descriptions.get(
        class_name,
        "No additional description available for this disease."
    )
