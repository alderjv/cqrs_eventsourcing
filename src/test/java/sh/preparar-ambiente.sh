# Certificar que existe um funcionário ativo
curl -X POST http://localhost:9000/api/funcionarios \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "529.982.247-25",
    "nome": "Ana Costa",
    "email": "ana@empresa.com",
    "cargo": "Desenvolvedora",
    "salario": 7000.00
  }' | jq

# Pegar o ID do funcionário
FUNC_ID=$(curl -s http://localhost:9000/api/funcionarios | jq -r '.[0].id')
echo "Funcionário ID: $FUNC_ID"