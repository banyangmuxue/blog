import { createRouter, createWebHistory } from 'vue-router'
import PublicLayout from '@/layouts/PublicLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { api } from '@/api'

const routes = [
  {
    path: '/admin/login',
    name: 'admin-login',
    component: () => import('@/views/admin/AdminLoginView.vue'),
    meta: { title: '管理登录' }
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: '',
        redirect: { name: 'admin-posts' }
      },
      {
        path: 'posts',
        name: 'admin-posts',
        component: () => import('@/views/admin/AdminPostListView.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'posts/new',
        name: 'admin-post-new',
        component: () => import('@/views/admin/AdminPostEditView.vue'),
        meta: { title: '新建文章' }
      },
      {
        path: 'posts/:id/edit',
        name: 'admin-post-edit',
        component: () => import('@/views/admin/AdminPostEditView.vue'),
        meta: { title: '编辑文章' }
      },
      {
        path: 'profile',
        name: 'admin-profile',
        component: () => import('@/views/admin/AdminProfileView.vue'),
        meta: { title: '个人资料' }
      },
      {
        path: 'site',
        name: 'admin-site',
        component: () => import('@/views/admin/AdminSiteView.vue'),
        meta: { title: '站点设置' }
      }
    ]
  },
  {
    path: '/',
    component: PublicLayout,
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/HomeView.vue'),
        meta: { title: '记录代码与生活' }
      },
      {
        path: 'articles',
        name: 'articles',
        component: () => import('@/views/ArticleListView.vue'),
        meta: { title: '文章' }
      },
      {
        path: 'post/:slug',
        name: 'post',
        component: () => import('@/views/ArticleDetailView.vue'),
        meta: { title: '文章详情' }
      },
      {
        path: 'categories/:slug',
        name: 'category',
        component: () => import('@/views/TaxonomyView.vue'),
        meta: { title: '分类' }
      },
      {
        path: 'tags',
        name: 'tags',
        component: () => import('@/views/TagsIndexView.vue'),
        meta: { title: '标签' }
      },
      {
        path: 'tags/:slug',
        name: 'tag',
        component: () => import('@/views/TaxonomyView.vue'),
        meta: { title: '标签' }
      },
      {
        path: 'search',
        name: 'search',
        component: () => import('@/views/SearchView.vue'),
        meta: { title: '搜索' }
      },
      {
        path: 'archive',
        name: 'archive',
        component: () => import('@/views/ArchiveView.vue'),
        meta: { title: '归档' }
      },
      {
        path: 'about',
        name: 'about',
        component: () => import('@/views/AboutView.vue'),
        meta: { title: '关于我' }
      },
      {
        path: 'links',
        name: 'links',
        component: () => import('@/views/LinksView.vue'),
        meta: { title: '友链' }
      },
      {
        path: 'guestbook',
        name: 'guestbook',
        component: () => import('@/views/GuestbookView.vue'),
        meta: { title: '留言板' }
      },
      {
        path: ':pathMatch(.*)*',
        name: 'not-found',
        component: () => import('@/views/NotFoundView.vue'),
        meta: { title: '页面不存在' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach(async (to) => {
  if (!to.path.startsWith('/admin') || to.name === 'admin-login') {
    return true
  }

  try {
    const session = await api.getAdminSession()
    if (session && session.loggedIn) {
      return true
    }
  } catch (error) {
    // 后端未启动或会话过期时统一回到登录页
  }
  return {
    name: 'admin-login',
    query: { redirect: to.fullPath }
  }
})

router.afterEach((to) => {
  const site = '记录代码与生活'
  document.title = to.meta.title ? `${to.meta.title} · ${site}` : site
})

export default router
