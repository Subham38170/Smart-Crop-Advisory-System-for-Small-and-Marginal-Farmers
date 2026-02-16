import pandas as pd
import streamlit as st
import joblib

# Load model
fertilizer_model = joblib.load("fertilizer_model.pkl")

# Fertilizer mapping
fertilizer_mapping = {
    0: 'Urea',
    1: 'DAP',
    2: '14-35-14',
    3: '28-28',
    4: '17-17-17',
    5: '20-20',
    6: '10-26-26'
}

# Function to predict NPK values
def predict_npk(example):
    soil = example['Soil Type']
    crop = example['Previous Crop']
    moisture = example['Moisture']  # numeric value

    # Base NPK values
    N, P, K = 30, 20, 15  

    # Adjust based on soil type
    if soil == 'Sandy':
        N -= 5; K -= 3
    elif soil == 'Clayey':
        P += 5
    elif soil == 'Black':
        K += 5
    elif soil == 'Red':
        N -= 3; P += 2

    # Adjust based on previous crop
    if "Legumes" in crop:
        N += 10  
    elif "Cereals" in crop:
        N -= 5

    # Adjust based on soil moisture
    N += (moisture - 50) * 0.1
    P += (moisture - 50) * 0.05
    K += (moisture - 50) * 0.05

    return [max(N, 0), max(P, 0), max(K, 0)]

# Function to predict fertilizer
def predict_fertilizer(example):
    df = pd.DataFrame([example])
    df = pd.get_dummies(df)

    model_columns = fertilizer_model.feature_names_in_
    for col in model_columns:
        if col not in df.columns:
            df[col] = 0
    df = df[model_columns]

    pred_num = fertilizer_model.predict(df)[0]
    pred_label = fertilizer_mapping.get(pred_num, "Unknown")
    return pred_label

# ---------------- Streamlit UI ---------------- #
st.title("Farmer-Friendly Fertilizer Recommendation 🌱")
st.subheader("Step 1: Farmer Field Information")

# Inputs
temperature = st.number_input("Temperature (°C)", 0, 50, 30)

# Humidity as dropdown
humidity_levels = ['Low', 'Medium', 'High']
humidity_text = st.selectbox("Humidity Level", humidity_levels)
humidity_map = {'Low': 30, 'Medium': 55, 'High': 80}
humidity = humidity_map[humidity_text]

# Soil moisture as dropdown
moisture_levels = ['Low', 'Medium', 'High']
soil_moisture_text = st.selectbox("Soil Moisture Level", moisture_levels)
moisture_map = {'Low': 30, 'Medium': 50, 'High': 70}
soil_moisture_value = moisture_map[soil_moisture_text]

# Other farmer inputs
soil_type = st.selectbox("Soil Type", ['Sandy', 'Loamy', 'Clayey', 'Black', 'Red'])
previous_crop = st.selectbox("Previous Crop", ['Legumes (Pulses, Soybean)', 'Cereals (Rice, Wheat, Maize)', 'Oilseeds', 'Vegetables', 'Other'])
manure_use = st.selectbox("Do you use Farmyard Manure/Compost?", ['Yes', 'No'])
irrigation = st.selectbox("Irrigation Type", ['Rainfed', 'Canal', 'Borewell'])
rainfall = st.selectbox("Rainfall Level", ['Low', 'Medium', 'High'])
crop_type = st.selectbox("Current Crop Type", ['Maize', 'Wheat', 'Rice', 'Vegetables', 'Other'])

# Prepare farmer input
example_farmer = {
    'Temperature': temperature,
    'Humidity': humidity,
    'Moisture': soil_moisture_value,
    'Soil Type': soil_type,
    'Previous Crop': previous_crop,
    'Manure Used': manure_use,
    'Irrigation': irrigation,
    'Rainfall': rainfall,
    'Crop Type': crop_type
}

# Prediction button
if st.button("Predict Fertilizer"):
    npk_values = predict_npk(example_farmer)
    st.write(f"📊 Predicted Soil Nutrients → Nitrogen (N): {npk_values[0]:.1f}, Phosphorous (P): {npk_values[1]:.1f}, Potassium (K): {npk_values[2]:.1f}")

    # Add predicted NPK to input for fertilizer prediction
    example_farmer['Nitrogen'] = npk_values[0]
    example_farmer['Phosphorous'] = npk_values[1]
    example_farmer['Potassium'] = npk_values[2]

    fertilizer_prediction = predict_fertilizer(example_farmer)
    st.success(f"🌾 Recommended Fertilizer: **{fertilizer_prediction}**")
