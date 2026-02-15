package com.example.krishimitra.data.mappers

import com.example.krishimitra.data.local.entity.MandiPriceEntity
import com.example.krishimitra.domain.model.mandi_data.MandiPriceDto


fun MandiPriceDto.toEntity(): MandiPriceEntity {
    return MandiPriceEntity(
        arrival_date = this.arrival_date,
        commodity = this.commodity,
        district = this.district,
        grade = this.grade,
        market = this.market,
        max_price = this.max_price,
        min_price = this.min_price,
        modal_price = this.modal_price,
        state = this.state,
        variety = this.variety
    )
}


fun MandiPriceEntity.toDto(): MandiPriceDto {
    return MandiPriceDto(
        arrival_date = this.arrival_date,
        commodity = this.commodity,
        district = this.district,
        grade = this.grade,
        market = this.market,
        max_price = this.max_price,
        min_price = this.min_price,
        modal_price = this.modal_price,
        state = this.state,
        variety = this.variety
    )
}