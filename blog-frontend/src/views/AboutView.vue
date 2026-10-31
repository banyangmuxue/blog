<template>
  <div class="about page-enter">
    <PageHero
      eyebrow="ABOUT"
      title="关于我"
      description="一个在写代码、也在学着把话说清楚的人。"
    />

    <div class="container">
      <div v-if="!ready" class="loading-block">
        <div class="skeleton line w30"></div>
        <div class="skeleton line w60"></div>
        <div class="skeleton block"></div>
      </div>

      <template v-else>
        <section class="profile">
          <div class="avatar">
            <img
              v-if="profile?.avatarUrl"
              :src="profile.avatarUrl"
              :alt="profile.name || '博主头像'"
            />
            <span v-else>{{ profile?.avatarText || 'L' }}</span>
          </div>
          <div class="intro">
            <h2>{{ profile?.name || '博主' }}</h2>
            <p class="bio">{{ profile?.bio }}</p>
            <div class="skills">
              <span v-for="skill in profile?.skills || []" :key="skill" class="skill">{{ skill }}</span>
            </div>
            <div class="socials">
              <a
                v-for="item in profile?.socials || []"
                :key="item.label"
                :href="item.url"
                :target="item.url.startsWith('http') ? '_blank' : undefined"
                rel="noopener noreferrer"
                class="social"
              >
                {{ item.label }} ↗
              </a>
              <router-link to="/guestbook" class="social">留言板 →</router-link>
            </div>
          </div>
          <dl class="facts">
            <div>
              <dt class="mono">SINCE</dt>
              <dd>{{ site.config.foundedAt || '2023' }}</dd>
            </div>
            <div>
              <dt class="mono">ROLE</dt>
              <dd>前端 / 后端</dd>
            </div>
            <div>
              <dt class="mono">STATUS</dt>
              <dd class="green">持续更新</dd>
            </div>
          </dl>
        </section>

        <section class="about-body">
          <MarkdownView :html="aboutHtml" />
        </section>

        <section class="contact">
          <div>
            <span class="eyebrow">CONTACT</span>
            <h3>想聊聊？</h3>
            <p>技术、写作、或者只是打个招呼，都可以在留言板留下信息。</p>
          </div>
          <div class="contact-actions">
            <router-link to="/guestbook" class="btn primary">去留言板</router-link>
            <a
              v-if="email"
              :href="`mailto:${email}`"
              class="btn ghost"
            >
              发邮件
            </a>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { loadSite, useSite } from '@/composables/useSite'
import { renderMarkdown } from '@/utils/markdown'
import PageHero from '@/components/common/PageHero.vue'
import MarkdownView from '@/components/common/MarkdownView.vue'

const { state: site } = useSite()
const ready = ref(false)

const profile = computed(() => site.profile)
const aboutHtml = computed(() =>
  profile.value?.about ? renderMarkdown(profile.value.about).html : ''
)
const email = computed(() => {
  const item = (site.config.socials || []).find((social) => social.url.startsWith('mailto:'))
  return item ? item.url.replace('mailto:', '') : ''
})

onMounted(async () => {
  await loadSite()
  ready.value = true
})
</script>

<style scoped lang="scss">
.loading-block .line {
  margin-bottom: 14px;
}

.w30 {
  width: 30%;
}

.w60 {
  width: 60%;
}

.block {
  height: 240px;
  margin-top: 24px;
}

.profile {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr) 150px;
  gap: 26px;
  align-items: center;
  padding: 26px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: linear-gradient(160deg, rgba(17, 27, 41, 0.7), rgba(10, 16, 24, 0.5));
}

.avatar {
  display: grid;
  overflow: hidden;
  width: 92px;
  height: 92px;
  place-items: center;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.45), rgba(139, 92, 246, 0.55));
  color: #04121a;
  font-family: var(--mono);
  font-size: 34px;
  font-weight: 700;
  box-shadow: 0 16px 40px -22px rgba(62, 231, 255, 0.6);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.intro {
  h2 {
    font-size: 24px;
    letter-spacing: -0.02em;
  }

  .bio {
    margin-top: 6px;
    color: var(--muted);
    font-size: 13px;
  }
}

.skills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}

.skill {
  padding: 2px 9px;
  border: 1px solid var(--line);
  border-radius: 999px;
  color: var(--muted);
  font-family: var(--mono);
  font-size: 10px;
}

.socials {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 14px;
}

.social {
  color: var(--cyan);
  font-size: 11.5px;

  &:hover {
    text-decoration: none;
    filter: brightness(1.15);
  }
}

.facts {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 0;
  padding-left: 20px;
  border-left: 1px solid var(--line);

  dt {
    color: var(--faint);
    font-size: 9px;
    letter-spacing: 0.1em;
  }

  dd {
    margin: 2px 0 0;
    color: var(--muted);
    font-size: 12px;

    &.green {
      color: var(--green);
    }
  }
}

.about-body {
  max-width: 800px;
  margin: 36px auto 0;
}

.contact {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  max-width: 800px;
  margin: 40px auto 0;
  padding: 22px 24px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.45);

  h3 {
    margin-top: 6px;
    font-size: 18px;
  }

  p {
    margin-top: 4px;
    color: var(--muted);
    font-size: 12.5px;
  }
}

.contact-actions {
  display: flex;
  gap: 10px;
  flex: none;
}

.btn {
  display: inline-flex;
  align-items: center;
  height: 36px;
  padding: 0 18px;
  border-radius: var(--radius);
  font-size: 12.5px;

  &.primary {
    border: 1px solid rgba(62, 231, 255, 0.45);
    background: linear-gradient(180deg, #54ecff, #25cfe8);
    color: #04121a;
    font-weight: 600;

    &:hover {
      filter: brightness(1.07);
      text-decoration: none;
    }
  }

  &.ghost {
    border: 1px solid var(--line);
    color: var(--muted);

    &:hover {
      color: var(--text);
      border-color: var(--line-strong);
      text-decoration: none;
    }
  }
}

@media (max-width: 900px) {
  .profile {
    grid-template-columns: 76px minmax(0, 1fr);
    gap: 18px;
  }

  .avatar {
    width: 72px;
    height: 72px;
    font-size: 26px;
  }

  .facts {
    grid-column: 1 / -1;
    flex-direction: row;
    gap: 32px;
    padding: 14px 0 0;
    border-left: 0;
    border-top: 1px solid var(--line);
  }
}

@media (max-width: 640px) {
  .contact {
    flex-direction: column;
    align-items: flex-start;
  }

  .contact-actions {
    width: 100%;

    .btn {
      flex: 1;
      justify-content: center;
    }
  }
}
</style>
