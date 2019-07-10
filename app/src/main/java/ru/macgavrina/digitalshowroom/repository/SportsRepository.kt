package ru.macgavrina.digitalshowroom.repository

import io.reactivex.Single
import ru.macgavrina.digitalshowroom.R
import ru.macgavrina.digitalshowroom.api.SportsDataService
import ru.macgavrina.digitalshowroom.model.Sport

object SportsRepository {

    private var allSports: Single<List<Sport>> = SportsDataService.create().getSportsList()
    private var sportIdToImageMap = mutableMapOf<Int, Int>()

    init {
        sportIdToImageMap[12] = R.drawable.sport_12
        sportIdToImageMap[37] = R.drawable.sport_37
        sportIdToImageMap[66] = R.drawable.sport_66
        sportIdToImageMap[70] = R.drawable.sport_70
        sportIdToImageMap[78] = R.drawable.sport_78
        sportIdToImageMap[81] = R.drawable.sport_81
        sportIdToImageMap[89] = R.drawable.sport_89
        sportIdToImageMap[93] = R.drawable.sport_93
        sportIdToImageMap[100] = R.drawable.sport_100
        sportIdToImageMap[109] = R.drawable.sport_109
        sportIdToImageMap[127] = R.drawable.sport_127
        sportIdToImageMap[132] = R.drawable.sport_132
        sportIdToImageMap[134] = R.drawable.sport_134
        sportIdToImageMap[160] = R.drawable.sport_160
        sportIdToImageMap[163] = R.drawable.sport_163
        sportIdToImageMap[166] = R.drawable.sport_166
        sportIdToImageMap[171] = R.drawable.sport_171
        sportIdToImageMap[175] = R.drawable.sport_175
        sportIdToImageMap[219] = R.drawable.sport_219
        sportIdToImageMap[224] = R.drawable.sport_224
        sportIdToImageMap[280] = R.drawable.sport_280
        sportIdToImageMap[286] = R.drawable.sport_286
        sportIdToImageMap[291] = R.drawable.sport_291
        sportIdToImageMap[292] = R.drawable.sport_292
        sportIdToImageMap[294] = R.drawable.sport_294
        sportIdToImageMap[299] = R.drawable.sport_299
        sportIdToImageMap[301] = R.drawable.sport_301
        sportIdToImageMap[302] = R.drawable.sport_302
        sportIdToImageMap[389] = R.drawable.sport_389
        sportIdToImageMap[425] = R.drawable.sport_425
        sportIdToImageMap[428] = R.drawable.sport_428
        sportIdToImageMap[443] = R.drawable.sport_443
        sportIdToImageMap[461] = R.drawable.sport_461
    }

    fun getAllSports(): Single<List<Sport>> {
        return allSports
    }

    fun getSportImageById(sportId: Int): Int? {
        return sportIdToImageMap[sportId]
    }
}