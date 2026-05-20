export default {
  path: "/knowledge",
  redirect: "/knowledge/index",
  meta: {
    icon: "",
    // showLink: false,
    title: "企业知识库",
    rank: 6
  },
  children: [
    {
      path: "/knowledge/index",
      name: "Knowledge",
      component: () => import("@/views/file/knowledge.vue"),
      meta: {
        title: "企业知识库"
      }
    },
  ]
} satisfies RouteConfigsTable;
