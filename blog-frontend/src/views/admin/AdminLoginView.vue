<template>
  <div class="login-page">
    <div class="login-grid" aria-hidden="true"></div>

    <section class="login-card page-enter">
      <div class="login-brand">
        <span class="brand-mark">
          <img v-if="logoUrl" :src="logoUrl" alt="站点 Logo" />
          <template v-else>{{ logoText }}</template>
        </span>
        <div>
          <p class="eyebrow">ADMIN CONSOLE</p>
          <h1>登录管理后台</h1>
        </div>
      </div>

      <p class="login-desc">
        登录后可以新建、编辑和发布文章。管理端仅博主本人使用。
      </p>

      <form class="login-form" @submit.prevent="submit">
        <label class="admin-field">
          <span>账号</span>
          <input
            v-model="form.username"
            class="admin-input"
            type="text"
            autocomplete="username"
            placeholder="请输入管理员账号"
            :disabled="loading"
          />
        </label>

        <label class="admin-field">
          <span>密码</span>
          <input
            v-model="form.password"
            class="admin-input"
            type="password"
            autocomplete="current-password"
            placeholder="请输入管理员密码"
            :disabled="loading"
          />
        </label>

        <p v-if="error" class="login-error" role="alert">{{ error }}</p>

        <button type="submit" class="admin-btn primary login-submit" :disabled="loading">
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>

      <div class="login-foot">
        <span class="mono">账号密码由后端 application.yaml 配置</span>
        <router-link to="/">← 返回博客</router-link>
      </div>
    </section>

    <ToastHost />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useSite } from '@/composables/useSite'
import ToastHost from '@/components/common/ToastHost.vue'

const route = useRoute()
const router = useRouter()
const { state: site, loadSite } = useSite()

const logoUrl = computed(() => site.config.logoUrl)
const logoText = computed(() => site.config.logoText || 'B/')

onMounted(() => {
  if (!site.loaded) loadSite()
})

const form = reactive({
  username: '',
  password: ''
})
const loading = ref(false)
const error = ref('')

async function submit() {
  if (!form.username.trim() || !form.password) {
    error.value = '请输入账号和密码'
    return
  }

  loading.value = true
  error.value = ''
  try {
    await api.adminLogin({
      username: form.username.trim(),
      password: form.password
    })

    const redirect = route.query.redirect
    if (typeof redirect === 'string' && redirect.startsWith('/admin') && redirect !== '/admin/login') {
      router.replace(redirect)
    } else {
      router.replace({ name: 'admin-posts' })
    }
  } catch (err) {
    error.value = err.message || '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  position: relative;
  display: grid;
  min-height: 100vh;
  padding: 40px 20px;
  place-items: center;
  overflow: hidden;
}

.login-grid {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image:
    linear-gradient(rgba(125, 211, 252, 0.045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(125, 211, 252, 0.045) 1px, transparent 1px);
  background-size: 48px 48px;
  -webkit-mask-image: radial-gradient(circle at 50% 34%, #000 0%, transparent 72%);
  mask-image: radial-gradient(circle at 50% 34%, #000 0%, transparent 72%);
}

.login-card {
  position: relative;
  width: min(430px, 100%);
  padding: 34px 34px 24px;
  border: 1px solid var(--line);
  border-radius: 16px;
  background:
    linear-gradient(150deg, rgba(62, 231, 255, 0.05), transparent 42%),
    var(--panel);
  box-shadow: 0 36px 90px -50px #000;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 14px;

  .eyebrow {
    margin-bottom: 5px;
  }

  h1 {
    font-size: 24px;
  }
}

.brand-mark {
  display: grid;
  width: 34px;
  height: 34px;
  flex: none;
  place-items: center;
  border-radius: 9px;
  background: linear-gradient(135deg, var(--cyan), var(--violet));
  color: #04121a;
  font-family: var(--mono);
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 0 22px -10px var(--cyan);
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.login-desc {
  margin-top: 20px;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.8;
}

.login-form {
  display: flex;
  margin-top: 26px;
  flex-direction: column;
  gap: 16px;
}

.login-error {
  padding: 9px 11px;
  border: 1px solid rgba(255, 125, 143, 0.28);
  border-radius: 7px;
  background: rgba(255, 125, 143, 0.07);
  color: var(--red);
  font-size: 12.5px;
}

.login-submit {
  width: 100%;
  margin-top: 4px;
  padding: 11px 16px;
}

.login-foot {
  display: flex;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--line);
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--faint);
  font-size: 11.5px;

  a {
    flex: none;
    color: var(--muted);
    font-size: 12px;

    &:hover {
      color: var(--cyan);
      text-decoration: none;
    }
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 28px 22px 20px;
  }

  .login-foot {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
