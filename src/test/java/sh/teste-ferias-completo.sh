#!/bin/bash
# scripts/test-ferias.sh

BASE_URL="http://localhost:9000/api/ferias"

echo "=========================================="
echo "  TESTE DE CICLO DE VIDA DE FÉRIAS       "
echo "=========================================="

# Pegar funcionário
FUNC_ID=$(curl -s http://localhost:9000/api/funcionarios | jq -r '.[0].id')
echo "Funcionário: $FUNC_ID"

# Calcular datas (30 dias no futuro, 15 dias de férias)
DATA_INICIO=$(date -v+30d +%Y-%m-%d 2>/dev/null || date -d "+30 days" +%Y-%m-%d)
DATA_FIM=$(date -v+44d +%Y-%m-%d 2>/dev/null || date -d "+44 days" +%Y-%m-%d)

echo ""
echo ">>> 1. SOLICITAR férias"
echo "    Período: $DATA_INICIO a $DATA_FIM"
echo ""

RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d "{
    \"funcionarioId\": \"$FUNC_ID\",
    \"dataInicio\": \"$DATA_INICIO\",
    \"dataFim\": \"$DATA_FIM\"
  }")

echo $RESPONSE | jq

FERIAS_ID=$(echo $RESPONSE | jq -r '.id')
echo ""
echo "Férias ID: $FERIAS_ID"

echo ""
echo ">>> 2. Verificar ESTADO INICIAL"
echo ""
curl -s "$BASE_URL/$FERIAS_ID" | jq

echo ""
echo ">>> 3. Listar PENDENTES de aprovação"
echo ""
curl -s "$BASE_URL/pendentes" | jq

echo ""
echo ">>> 4. APROVAR férias"
echo ""
curl -s -X PUT "$BASE_URL/$FERIAS_ID/aprovar" \
  -H "Content-Type: application/json" \
  -d '{
    "aprovadoPor": "gerente@empresa.com",
    "observacao": "Aprovado conforme política da empresa"
  }' | jq

echo ""
echo ">>> 5. Verificar ESTADO após aprovação"
echo ""
curl -s "$BASE_URL/$FERIAS_ID" | jq

echo ""
echo ">>> 6. INICIAR gozo de férias"
echo ""
curl -s -X PUT "$BASE_URL/$FERIAS_ID/iniciar" | jq

echo ""
echo ">>> 7. Verificar ESTADO durante gozo"
echo ""
curl -s "$BASE_URL/$FERIAS_ID" | jq

echo ""
echo ">>> 8. CONCLUIR férias"
echo ""
curl -s -X PUT "$BASE_URL/$FERIAS_ID/concluir" | jq

echo ""
echo ">>> 9. Verificar ESTADO FINAL"
echo ""
curl -s "$BASE_URL/$FERIAS_ID" | jq

echo ""
echo "=========================================="
echo "  VERIFICAÇÃO NO BANCO DE DADOS          "
echo "=========================================="

echo ""
echo ">>> EVENTOS (histórico completo)"
echo ""
docker exec -it rh-postgres psql -U dev -d rh_training -c "
SELECT
  sequence_number as seq,
  event_type,
  occurred_on::timestamp(0) as quando
FROM rh.domain_events
WHERE aggregate_id = '$FERIAS_ID'
ORDER BY sequence_number;
"

echo ""
echo ">>> PROJEÇÃO (estado atual)"
echo ""
docker exec -it rh-postgres psql -U dev -d rh_training -c "
SELECT
  id,
  status,
  data_inicio,
  data_fim,
  dias_solicitados,
  aprovado_por
FROM rh.ferias
WHERE id = '$FERIAS_ID'::uuid;
"

echo ""
echo "=========================================="
echo "  TESTE CONCLUÍDO                        "
echo "=========================================="