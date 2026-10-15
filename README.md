# Meu consumo de água

Aplicativo Android desenvolvido em Java para registrar o consumo diário de água,
definir uma meta e acompanhar o progresso por meio de uma `WaterProgressView`.

Alunas: Glenda Santana e Letícia Almeida.

## Persistência

O aplicativo utiliza `SharedPreferences` porque armazena poucos dados simples:
a meta, a data atual, o consumo acumulado e uma lista curta de registros do dia.
Esse mecanismo mantém os dados após o fechamento do aplicativo e evita a
complexidade desnecessária de um banco de dados para este caso.

Os registros guardam o volume consumido e o instante da operação. Quando a data
do sistema muda, os registros e o acumulado do dia anterior são descartados, mas
a meta diária é preservada.
