from pydantic import BaseModel
class FertilizerInput(BaseModel):
    state: str
    soil_type: str
    previous_crop: str
    fertilizer_used: str
    manure_used: str
    irrigation: str
    rainfall: str
    crop_type: str


class MarketTrend(BaseModel):
    state: str
    district: str
    market: str
    commodity: str