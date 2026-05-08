<template>
  <div class="page-shell">
    <NuxtRouteAnnouncer />

    <header class="hero">
      <div>
        <h1>Trivia</h1>
      </div>
      <button class="primary-button" @click="fetchQuestions" :disabled="loading">
        {{ loading ? 'Loading...' : 'New Question' }}
      </button>
    </header>

    <section v-if="error" class="status-banner error-banner">
      <strong>Error</strong>
      <span>{{ error.message || 'An error occurred.' }}</span>
      <small v-if="error.status">Status: {{ error.status }}</small>
    </section>

    <section v-if="result" class="status-banner result-banner" :class="result.correct ? 'success-banner' : 'failure-banner'">
      <strong>{{ result.correct ? 'Correct!' : 'Incorrect' }}</strong>
    </section>

    <section v-if="questions && questions.results?.length" class="trivia-container">
      <article v-for="(question, qIndex) in questions.results" :key="qIndex" class="question-card">
        <div class="question-meta">
          <span>{{ question.category }} · {{ question.difficulty }}</span>
        </div>
        <h2>{{ decodeHtml(question.question) }}</h2>

        <form @submit.prevent="submitAnswer(qIndex)">
          <ul class="answers-list">
            <li v-for="(answer, ansIndex) in question.answers ?? []" :key="ansIndex">
              <label :class="['answer-item', { selected: selectedAnswers[qIndex] === answer }]">
                <input
                  type="radio"
                  :name="`answer-${qIndex}`"
                  :value="answer"
                  v-model="selectedAnswers[qIndex]"
                />
                <span>{{ decodeHtml(answer) }}</span>
              </label>
            </li>
          </ul>

          <button class="submit-button" type="submit" :disabled="submitting || !selectedAnswers[qIndex]">
            {{ submitting ? 'Submitting...' : 'Submit Answer' }}
          </button>
        </form>
      </article>
    </section>

    <section v-else class="empty-state">
    </section>
  </div>
</template>

<script setup>
const questions = ref(null)
const selectedAnswers = ref({})
const result = ref(null)
const error = ref(null)
const loading = ref(false)
const submitting = ref(false)

const fetchQuestions = async () => {
  loading.value = true
  error.value = null
  result.value = null
  selectedAnswers.value = {}

  try {
    questions.value = await $fetch('http://localhost:8080/questions', {
      credentials: 'include',
    })
  } catch (err) {
    console.error('Error fetching questions:', err)
    error.value = parseFetchError(err)
  } finally {
    loading.value = false
  }
}

const submitAnswer = async (questionIndex) => {
  const question = questions.value?.results?.[questionIndex]
  const providedAnswer = selectedAnswers.value[questionIndex]

  if (!question || !providedAnswer) {
    error.value = { message: 'Please select an answer before submitting.' }
    return
  }

  submitting.value = true
  error.value = null
  result.value = null

  try {
    const response = await fetch('http://localhost:8080/checkanswers', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ provided_answer: providedAnswer }),
      credentials: 'include'
    })

    const body = await response.json()

    if (!response.ok || body?.status) {
      error.value = {
        status: body?.status || response.status,
        message: body?.message || 'An error occurred.'
      }
      return
    }

    result.value = { correct: body.correct }
  } catch (err) {
    console.error('An error occurred: ', err)
    error.value = { message: err.message || 'An error occurred.' }
  } finally {
    submitting.value = false
  }
}

const decodeHtml = (html) => {
  if (!html || typeof html !== 'string') {
    return html
  }

  if (typeof document === 'undefined') {
    return html
  }

  const textarea = document.createElement('textarea')
  textarea.innerHTML = html
  return textarea.value
}

const parseFetchError = (err) => {
  if (err?.data?.status) {
    return { status: err.data.status, message: err.data.message || 'An error occurred.' }
  }

  if (err?.statusCode) {
    return { status: err.statusCode, message: err.message }
  }

  return { message: err?.message || 'An error occurred.' }
}
</script>

<style scoped>
.page-shell {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
  font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  color: #1f2937;
  background: #f3f4f6;
  min-height: 100vh;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  padding: 24px;
  background: linear-gradient(135deg, #2563eb 0%, #4338ca 100%);
  border-radius: 18px;
  color: white;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.18em;
  font-size: 0.8rem;
  opacity: 0.85;
  margin-bottom: 12px;
}

.hero h1 {
  margin: 0;
  font-size: clamp(2rem, 2.5vw, 3rem);
}

.subtitle {
  margin: 12px 0 0;
  max-width: 560px;
  line-height: 1.7;
  opacity: 0.95;
}

.primary-button,
.submit-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  border-radius: 999px;
  padding: 14px 22px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease, background-color 0.15s ease;
}

.primary-button {
  background: white;
  color: #1f2937;
}

.primary-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.15);
}

.primary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.trivia-container {
  display: grid;
  gap: 20px;
}

.question-card {
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(15, 23, 42, 0.05);
}

.question-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 18px;
  color: #6b7280;
  font-size: 0.95rem;
}

.question-type {
  background: #eef2ff;
  color: #4338ca;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.85rem;
}

.question-card h2 {
  margin: 0 0 20px;
  font-size: 1.5rem;
  line-height: 1.3;
}

.answers-list {
  display: grid;
  gap: 12px;
  list-style: none;
  margin: 0;
  padding: 0;
}

.answer-item {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 16px 18px;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  cursor: pointer;
  transition: transform 0.15s ease, border-color 0.15s ease, background-color 0.15s ease;
  box-sizing: border-box;
  min-width: 0;
}

.answer-item:hover {
  transform: translateY(-1px);
  border-color: #c7d2fe;
}

.answer-item span {
  flex: 1;
  word-break: break-word;
  white-space: normal;
}

.answer-item.selected {
  border-color: #2563eb;
  background: #eff6ff;
}

.answer-item input {
  accent-color: #2563eb;
}

.answer-item span {
  flex: 1;
  font-size: 1rem;
  color: #111827;
}

.submit-button {
  margin-top: 20px;
  background: #2563eb;
  color: white;
  width: fit-content;
}

.submit-button:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.status-banner {
  border-radius: 16px;
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 20px;
}

.error-banner {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #991b1b;
}

.success-banner {
  background: #ecfdf5;
  border: 1px solid #6ee7b7;
  color: #047857;
}

.failure-banner {
  background: #fef3c7;
  border: 1px solid #facc15;
  color: #92400e;
}

.empty-state {
  text-align: center;
  padding: 48px 0;
  color: #4b5563;
  font-size: 1rem;
}

@media (max-width: 760px) {
  .hero {
    padding: 20px;
  }

  .question-card {
    padding: 22px;
  }
}
</style>
