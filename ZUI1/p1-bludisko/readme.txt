MyAgent
- agentovi na zaciatku vytvorime pamat podla velkosti mapy. Pamat je dvojrozmerne pole ktore je 2x vacsie ako 
cela mapa. Dovod -> neviem zaciatocnu poziciu agenta

- pri kazdom pohybe sa pamat rozsiruje podla noveho percept. Tzn. postupne sa odhaluje to co je nezname = UKNOWN
- pri kazdom rozsireni pamate sa testuju nove policka ci sme nenasli nejake DIRTY miesto alebo nejake
CLEAN miesto, ktore je tesne vedla UNKNOWN

- v kazdom vykonavaci metody Act vyberam prioritne najblizsie spinave miesto. Ak nenajdem DIRTY vyberiem najblizsie 
CLEAN miesto ktore je vedla niecoho co je UNKNOWN.
Potom sa snazim najst najblizsiu cestu k tomuto miesto. Ak sa mi cestu nepodari vypocitat , zatial na toto miesto zabudnem
a pokracujem s dalsimi.

Na pocitanie cesty pouzivam vyhladavanie do sirky s heuristikou.

Ak agent nema uz v pamati ziadne "CLEAN next to UKNOWN" a ziadne DIRTY miesto, tak sa zavola metoda HALT.


Vo vyhladavni pouzivam priority queue, kde trieda Field ma implementovany interface comparable, v ktorom sa prioritizuje podla

